/*
 * The JTS Topology Suite is a collection of Java classes that
 * implement the fundamental operations required to validate a given
 * geo-spatial data set to a known topological specification.
 * 
 * Copyright (C) 2016 Vivid Solutions
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Vivid Solutions BSD
 * License v1.0 (found at the root of the repository).
 * 
 */
package org.locationtech.jts.operation.buffer;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateArrays;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geomgraph.Position;

/**
 * Computes the raw offset curve for a
 * single {@link Geometry} component (ring, line or point).
 * A raw offset curve line is not noded -
 * it may contain self-intersections (and usually will).
 * The final buffer polygon is computed by forming a topological graph
 * of all the noded raw curves and tracing outside contours.
 * The points in the raw curve are rounded 
 * to a given {@link PrecisionModel}.
 *
 * @version 1.7
 */
public class OffsetCurveBuilder 
{  
  private double distance = 0.0;
  private PrecisionModel precisionModel;
  private BufferParameters bufParams;
  
  public OffsetCurveBuilder(
                PrecisionModel precisionModel,
                BufferParameters bufParams
                )
  {
    this.precisionModel = precisionModel;
    this.bufParams = bufParams;
  }

  /**
   * Gets the buffer parameters being used to generate the curve.
   * 
   * @return the buffer parameters being used
   */
  public BufferParameters getBufferParameters()
  {
    return bufParams;
  }
  
  /**
   * This method handles single points as well as LineStrings.
   * LineStrings are assumed <b>not</b> to be closed (the function will not
   * fail for closed lines, but will generate superfluous line caps).
   *
   * @param inputPts the vertices of the line to offset
   * @param distance the offset distance
   * 
   * @return a Coordinate array representing the curve
   * or null if the curve is empty
   */
  public Coordinate[] getLineCurve(Coordinate[] inputPts, double distance)
  {
    this.distance = distance;
    
    // a zero or negative width buffer of a line/point is empty
    if (distance < 0.0 && ! bufParams.isSingleSided()) return null;
    if (distance == 0.0) return null;

    double posDistance = Math.abs(distance);
    OffsetSegmentGenerator segGen = getSegGen(posDistance);
    if (inputPts.length <= 1) {
      computePointCurve(inputPts[0], segGen);
    }
    else {
      if (bufParams.isSingleSided()) {
        boolean isRightSide = distance < 0.0;
        computeSingleSidedBufferCurve(inputPts, isRightSide, segGen);
      }
      else
        computeLineBufferCurve(inputPts, segGen);
    }
    
    Coordinate[] lineCoord = segGen.getCoordinates();
    return lineCoord;
  }

  /**
   * This method handles the degenerate cases of single points and lines,
   * as well as rings.
   *
   * @return a Coordinate array representing the curve
   * or null if the curve is empty
   */
  public Coordinate[] getRingCurve(Coordinate[] inputPts, int side, double distance)
  {
    this.distance = distance;
    if (inputPts.length <= 2)
      return getLineCurve(inputPts, distance);

    // optimize creating ring for for zero distance
    if (distance == 0.0) {
      return copyCoordinates(inputPts);
    }
    OffsetSegmentGenerator segGen = getSegGen(distance);
    computeRingBufferCurve(inputPts, side, segGen);
    return segGen.getCoordinates();
  }

  public Coordinate[] getOffsetCurve(Coordinate[] inputPts, double distance)
  {
    this.distance = distance;
    
    // a zero width offset curve is empty
    if (distance == 0.0) return null;

    boolean isRightSide = distance < 0.0;
    double posDistance = Math.abs(distance);
    OffsetSegmentGenerator segGen = getSegGen(posDistance);
    if (inputPts.length <= 1) {
      computePointCurve(inputPts[0], segGen);
    }
    else {
      computeOffsetCurve(inputPts, isRightSide, segGen);
    }
    Coordinate[] curvePts = segGen.getCoordinates();
    // for right side line is traversed in reverse direction, so have to reverse generated line
    if (isRightSide) 
      CoordinateArrays.reverse(curvePts);
    return curvePts;
  }

  private static Coordinate[] copyCoordinates(Coordinate[] pts)
  {
    Coordinate[] copy = new Coordinate[pts.length];
    for (int i = 0; i < copy.length; i++) {
      copy[i] = new Coordinate(pts[i]);
    }
    return copy;
  }
    
  private OffsetSegmentGenerator getSegGen(double distance)
  {
    return new OffsetSegmentGenerator(precisionModel, bufParams, distance);
  }
  
  /**
   * Computes the distance tolerance to use during input
   * line simplification.
   * 
   * @param distance the buffer distance
   * @return the simplification tolerance
   */
  private double simplifyTolerance(double bufDistance)
  {
    return bufDistance * bufParams.getSimplifyFactor();
  }
  
  private void computePointCurve(Coordinate pt, OffsetSegmentGenerator segGen) {
    switch (bufParams.getEndCapStyle()) {
      case BufferParameters.CAP_ROUND:
        segGen.createCircle(pt);
        break;
      case BufferParameters.CAP_SQUARE:
        segGen.createSquare(pt);
        break;
      // otherwise curve is empty (e.g. for a butt cap);
    }
  }

  private void computeLineBufferCurve(Coordinate[] inputPts, OffsetSegmentGenerator segGen)
  {
    double distTol = simplifyTolerance(distance);
    
    //--------- compute points for left side of line
    // Simplify the appropriate side of the line before generating
    Coordinate[] simp1 = BufferInputLineSimplifier.simplify(inputPts, distTol);
    // MD - used for testing only (to eliminate simplification)
//    Coordinate[] simp1 = inputPts;
    
    int n1 = simp1.length - 1;
    segGen.initSideSegments(simp1[0], simp1[1], Position.LEFT);
    for (int i = 2; i <= n1; i++) {
      segGen.addNextSegment(simp1[i], true);
    }
    segGen.addLastSegment();
    // add line cap for end of line
    segGen.addLineEndCap(simp1[n1 - 1], simp1[n1]);
    
    //---------- compute points for right side of line
    // Simplify the appropriate side of the line before generating
    Coordinate[] simp2 = BufferInputLineSimplifier.simplify(inputPts, -distTol);
    // MD - used for testing only (to eliminate simplification)
//    Coordinate[] simp2 = inputPts;
    int n2 = simp2.length - 1;
   
    // since we are traversing line in opposite order, offset position is still LEFT
    segGen.initSideSegments(simp2[n2], simp2[n2 - 1], Position.LEFT);
    for (int i = n2 - 2; i >= 0; i--) {
      segGen.addNextSegment(simp2[i], true);
    }
    segGen.addLastSegment();
    // add line cap for start of line
    segGen.addLineEndCap(simp2[1], simp2[0]);

    segGen.closeRing();
  }

  /*
  private void OLDcomputeLineBufferCurve(Coordinate[] inputPts)
  {
    int n = inputPts.length - 1;
    
    // compute points for left side of line
    initSideSegments(inputPts[0], inputPts[1], Position.LEFT);
    for (int i = 2; i <= n; i++) {
      addNextSegment(inputPts[i], true);
    }
    addLastSegment();
    // add line cap for end of line
    addLineEndCap(inputPts[n - 1], inputPts[n]);

    // compute points for right side of line
    initSideSegments(inputPts[n], inputPts[n - 1], Position.LEFT);
    for (int i = n - 2; i >= 0; i--) {
      addNextSegment(inputPts[i], true);
    }
    addLastSegment();
    // add line cap for start of line
    addLineEndCap(inputPts[1], inputPts[0]);

    vertexList.closeRing();
  }
  */
  
  private void computeSingleSidedBufferCurve(Coordinate[] inputPts, boolean isRightSide, OffsetSegmentGenerator segGen)
  {
    double distTol = simplifyTolerance(distance);
    
    if (isRightSide) {
      // add original line
      segGen.addSegments(inputPts, true);
      
      //---------- compute points for right side of line
      // Simplify the appropriate side of the line before generating
      Coordinate[] simp2 = BufferInputLineSimplifier.simplify(inputPts, -distTol);
      // MD - used for testing only (to eliminate simplification)
  //    Coordinate[] simp2 = inputPts;
      int n2 = simp2.length - 1;
     
      // since we are traversing line in opposite order, offset position is still LEFT
      segGen.initSideSegments(simp2[n2], simp2[n2 - 1], Position.LEFT);
      segGen.addFirstSegment();
      for (int i = n2 - 2; i >= 0; i--) {
        segGen.addNextSegment(simp2[i], true);
      }
    }
    else {
      // add original line
      segGen.addSegments(inputPts, false);
      
      //--------- compute points for left side of line
      // Simplify the appropriate side of the line before generating
      Coordinate[] simp1 = BufferInputLineSimplifier.simplify(inputPts, distTol);
      // MD - used for testing only (to eliminate simplification)
//      Coordinate[] simp1 = inputPts;
      
      int n1 = simp1.length - 1;
      segGen.initSideSegments(simp1[0], simp1[1], Position.LEFT);
      segGen.addFirstSegment();
      for (int i = 2; i <= n1; i++) {
        segGen.addNextSegment(simp1[i], true);
      }
    }
    segGen.addLastSegment();
    segGen.closeRing();
  }

  private void computeOffsetCurve(Coordinate[] inputPts, boolean isRightSide, OffsetSegmentGenerator segGen)
  {
    double distTol = simplifyTolerance(distance);
    
    if (isRightSide) {
      //---------- compute points for right side of line
      // Simplify the appropriate side of the line before generating
      Coordinate[] simp2 = BufferInputLineSimplifier.simplify(inputPts, -distTol);
      // MD - used for testing only (to eliminate simplification)
  //    Coordinate[] simp2 = inputPts;
      int n2 = simp2.length - 1;
     
      // since we are traversing line in opposite order, offset position is still LEFT
      segGen.initSideSegments(simp2[n2], simp2[n2 - 1], Position.LEFT);
      segGen.addFirstSegment();
      for (int i = n2 - 2; i >= 0; i--) {
        segGen.addNextSegment(simp2[i], true);
      }
    }
    else {
      //--------- compute points for left side of line
      // Simplify the appropriate side of the line before generating
      Coordinate[] simp1 = BufferInputLineSimplifier.simplify(inputPts, distTol);
      // MD - used for testing only (to eliminate simplification)
//      Coordinate[] simp1 = inputPts;
      
      int n1 = simp1.length - 1;
      segGen.initSideSegments(simp1[0], simp1[1], Position.LEFT);
      segGen.addFirstSegment();
      for (int i = 2; i <= n1; i++) {
        segGen.addNextSegment(simp1[i], true);
      }
    }
    segGen.addLastSegment();
  }

  private void computeRingBufferCurve(Coordinate[] inputPts, int side, OffsetSegmentGenerator segGen)
  {
    // simplify input line to improve performance
    double distTol = simplifyTolerance(distance);
    // ensure that correct side is simplified
    if (side == Position.RIGHT)
      distTol = -distTol;
    Coordinate[] simp = BufferInputLineSimplifier.simplify(inputPts, distTol);
//    Coordinate[] simp = inputPts;
    
    int n = simp.length - 1;
    segGen.initSideSegments(simp[n - 1], simp[0], side);
    for (int i = 1; i <= n; i++) {
      boolean addStartPoint = i != 1;
      segGen.addNextSegment(simp[i], addStartPoint);
    }
    segGen.closeRing();
  }


}
