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
package org.locationtech.jts.operation.buffer.validate;

import java.util.*;

import org.locationtech.jts.algorithm.distance.*;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.util.*;
import org.locationtech.jts.io.*;
import org.locationtech.jts.operation.distance.*;

/**
 * Validates that a given buffer curve lies an appropriate distance
 * from the input generating it. 
 * Useful only for round buffers (cap and join).
 * Can be used for either positive or negative distances.
 * <p>
 * This is a heuristic test, and may return false positive results
 * (I.e. it may fail to detect an invalid result.)
 * It should never return a false negative result, however
 * (I.e. it should never report a valid result as invalid.)
 * 
 * @author mbdavis
 *
 */
public class BufferDistanceValidator 
{
  private static boolean VERBOSE = false;
	/**
	 * Maximum allowable fraction of buffer distance the 
	 * actual distance can differ by.
	 * 1% sometimes causes an error - 1.2% should be safe.
	 */
	private static final double MAX_DISTANCE_DIFF_FRAC = .012;

  private Geometry input;
  private double bufDistance;
  private Geometry result;
  
  private double minValidDistance;
  private double maxValidDistance;
  
  private double minDistanceFound;
  private double maxDistanceFound;
  
  private boolean isValid = true;
  private String errMsg = null;
  private Coordinate errorLocation = null;
  private Geometry errorIndicator = null;
  
  public BufferDistanceValidator(Geometry input, double bufDistance, Geometry result)
  {
  	this.input = input;
  	this.bufDistance = bufDistance;
  	this.result = result;
  }
  
  public boolean isValid()
  {
  	double posDistance = Math.abs(bufDistance);
  	double distDelta = MAX_DISTANCE_DIFF_FRAC * posDistance;
  	minValidDistance = posDistance - distDelta;
  	maxValidDistance = posDistance + distDelta;
  	
  	// can't use this test if either is empty
  	if (input.isEmpty() || result.isEmpty())
  		return true;
  	
  	if (bufDistance > 0.0) {
  		checkPositiveValid();
  	}
  	else {
  		checkNegativeValid();
  	}
    if (VERBOSE) {
      System.out.println("Min Dist= " + minDistanceFound + "  err= " 
        + (1.0 - minDistanceFound / bufDistance) 
        + "  Max Dist= " + maxDistanceFound + "  err= " 
        + (maxDistanceFound / bufDistance - 1.0)
        );
    }
  	return isValid;
  }
  
  public String getErrorMessage()
  { 
  	return errMsg;
  }
  
  public Coordinate getErrorLocation()
  {
    return errorLocation;
  }
  
  /**
   * Gets a geometry which indicates the location and nature of a validation failure.
   * <p>
   * The indicator is a line segment showing the location and size
   * of the distance discrepancy.
   * 
   * @return a geometric error indicator
   * or null if no error was found
   */
  public Geometry getErrorIndicator()
  {
    return errorIndicator;
  }
  
  private void checkPositiveValid()
  {
  	Geometry bufCurve = result.getBoundary();
  	checkMinimumDistance(input, bufCurve, minValidDistance);
  	if (! isValid) return;
  	
  	checkMaximumDistance(input, bufCurve, maxValidDistance);
  }
  
  private void checkNegativeValid()
  {
  	// Assert: only polygonal inputs can be checked for negative buffers
  	
  	// MD - could generalize this to handle GCs too
  	if (! (input instanceof Polygon 
  			|| input instanceof MultiPolygon
  			|| input instanceof GeometryCollection
  			)) {
  		return;
  	}
  	Geometry inputCurve = getPolygonLines(input);
  	checkMinimumDistance(inputCurve, result, minValidDistance);
  	if (! isValid) return;
  	
  	checkMaximumDistance(inputCurve, result, maxValidDistance);
  }
  
  private Geometry getPolygonLines(Geometry g)
  {
  	List lines = new ArrayList();
  	LinearComponentExtracter lineExtracter = new LinearComponentExtracter(lines);
  	List polys = PolygonExtracter.getPolygons(g);
  	for (Iterator i = polys.iterator(); i.hasNext(); ) {
  		Polygon poly = (Polygon) i.next();
  		poly.apply(lineExtracter);
  	}
  	return g.getFactory().buildGeometry(lines);
  }
  
  /**
   * Checks that two geometries are at least a minumum distance apart.
   * 
   * @param g1 a geometry
   * @param g2 a geometry
   * @param minDist the minimum distance the geometries should be separated by
   */
  private void checkMinimumDistance(Geometry g1, Geometry g2, double minDist)
  {
  	DistanceOp distOp = new DistanceOp(g1, g2, minDist);
  	minDistanceFound = distOp.distance();
    
    
  	if (minDistanceFound < minDist) {
  		isValid = false;
  		Coordinate[] pts = distOp.nearestPoints();
  		errorLocation = distOp.nearestPoints()[1];
  		errorIndicator = g1.getFactory().createLineString(pts);
  		errMsg = "Distance between buffer curve and input is too small "
  			+ "(" + minDistanceFound
  			+ " at " + WKTWriter.toLineString(pts[0], pts[1]) +" )";
  	}
  }
  
  /**
   * Checks that the furthest distance from the buffer curve to the input
   * is less than the given maximum distance.
   * This uses the Oriented Hausdorff distance metric.
   * It corresponds to finding
   * the point on the buffer curve which is furthest from <i>some</i> point on the input.
   * 
   * @param input a geometry
   * @param bufCurve a geometry
   * @param maxDist the maximum distance that a buffer result can be from the input
   */
  private void checkMaximumDistance(Geometry input, Geometry bufCurve, double maxDist)
  {
//    BufferCurveMaximumDistanceFinder maxDistFinder = new BufferCurveMaximumDistanceFinder(input);
//    maxDistanceFound = maxDistFinder.findDistance(bufCurve);
    
    DiscreteHausdorffDistance haus = new DiscreteHausdorffDistance(bufCurve, input);
    haus.setDensifyFraction(0.25);
    maxDistanceFound = haus.orientedDistance();
    
    if (maxDistanceFound > maxDist) {
      isValid = false;
      Coordinate[] pts = haus.getCoordinates();
      errorLocation = pts[1];
      errorIndicator = input.getFactory().createLineString(pts);
      errMsg = "Distance between buffer curve and input is too large "
        + "(" + maxDistanceFound
        + " at " + WKTWriter.toLineString(pts[0], pts[1]) +")";
    }
  }
  
  /*
  private void OLDcheckMaximumDistance(Geometry input, Geometry bufCurve, double maxDist)
  {
    BufferCurveMaximumDistanceFinder maxDistFinder = new BufferCurveMaximumDistanceFinder(input);
    maxDistanceFound = maxDistFinder.findDistance(bufCurve);
    
    
    if (maxDistanceFound > maxDist) {
      isValid = false;
      PointPairDistance ptPairDist = maxDistFinder.getDistancePoints();
      errorLocation = ptPairDist.getCoordinate(1);
      errMsg = "Distance between buffer curve and input is too large "
        + "(" + ptPairDist.getDistance()
        + " at " + ptPairDist.toString() +")";
    }
  }
  */
  
  
}
