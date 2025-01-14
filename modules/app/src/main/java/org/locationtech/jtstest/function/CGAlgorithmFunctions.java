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

package org.locationtech.jtstest.function;

import org.locationtech.jts.algorithm.CGAlgorithms;
import org.locationtech.jts.algorithm.CGAlgorithmsDD;
import org.locationtech.jts.algorithm.RobustLineIntersector;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

public class CGAlgorithmFunctions
{
  public static int orientationIndex(Geometry segment, Geometry ptGeom) {
    if (segment.getNumPoints() != 2 || ptGeom.getNumPoints() != 1) {
      throw new IllegalArgumentException("A must have two points and B must have one");
    }
    Coordinate[] segPt = segment.getCoordinates();
    
    Coordinate p = ptGeom.getCoordinate();
    int index = CGAlgorithms.orientationIndex(segPt[0], segPt[1], p);
    return index;
  }

  public static int orientationIndexDD(Geometry segment, Geometry ptGeom) {
    if (segment.getNumPoints() != 2 || ptGeom.getNumPoints() != 1) {
      throw new IllegalArgumentException("A must have two points and B must have one");
    }
    Coordinate[] segPt = segment.getCoordinates();
    
    Coordinate p = ptGeom.getCoordinate();
    int index = CGAlgorithmsDD.orientationIndex(segPt[0], segPt[1], p);
    return index;
  }

  public static boolean segmentIntersects(Geometry g1, Geometry g2)
  {
    Coordinate[] pt1 = g1.getCoordinates();
    Coordinate[] pt2 = g2.getCoordinates();
    RobustLineIntersector ri = new RobustLineIntersector();
    ri.computeIntersection(pt1[0], pt1[1], pt2[0], pt2[1]);
    return ri.hasIntersection();
  }
  
  public static Geometry segmentIntersection(Geometry g1, Geometry g2)
  {
    Coordinate[] pt1 = g1.getCoordinates();
    Coordinate[] pt2 = g2.getCoordinates();
    RobustLineIntersector ri = new RobustLineIntersector();
    ri.computeIntersection(pt1[0], pt1[1], pt2[0], pt2[1]);
    switch (ri.getIntersectionNum()) {
    case 0:
      // no intersection => return empty point
      return g1.getFactory().createPoint((Coordinate) null);
    case 1:
      // return point
      return g1.getFactory().createPoint(ri.getIntersection(0));
    case 2:
      // return line
      return g1.getFactory().createLineString(
          new Coordinate[] {
              ri.getIntersection(0),
              ri.getIntersection(1)
          });
    }
    return null;
  }
  
  public static Geometry segmentIntersectionDD(Geometry g1, Geometry g2)
  {
    Coordinate[] pt1 = g1.getCoordinates();
    Coordinate[] pt2 = g2.getCoordinates();
    
    // first check if there actually is an intersection
    RobustLineIntersector ri = new RobustLineIntersector();
    ri.computeIntersection(pt1[0], pt1[1], pt2[0], pt2[1]);
    if (! ri.hasIntersection()) {
      // no intersection => return empty point
      return g1.getFactory().createPoint((Coordinate) null);
    }
    
    Coordinate intPt = CGAlgorithmsDD.intersection(pt1[0], pt1[1], pt2[0], pt2[1]);
    return g1.getFactory().createPoint(intPt);
  }
  
  
}
