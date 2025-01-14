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

package org.locationtech.jts.simplify;

import org.locationtech.jts.geom.*;

/**
 * Simplifies a linestring (sequence of points) using
 * the standard Douglas-Peucker algorithm.
 *
 * @version 1.7
 */
class DouglasPeuckerLineSimplifier
{
  public static Coordinate[] simplify(Coordinate[] pts, double distanceTolerance)
  {
    DouglasPeuckerLineSimplifier simp = new DouglasPeuckerLineSimplifier(pts);
    simp.setDistanceTolerance(distanceTolerance);
    return simp.simplify();
  }

  private Coordinate[] pts;
  private boolean[] usePt;
  private double distanceTolerance;

  public DouglasPeuckerLineSimplifier(Coordinate[] pts)
  {
    this.pts = pts;
  }
  /**
   * Sets the distance tolerance for the simplification.
   * All vertices in the simplified linestring will be within this
   * distance of the original linestring.
   *
   * @param distanceTolerance the approximation tolerance to use
   */
  public void setDistanceTolerance(double distanceTolerance) {
    this.distanceTolerance = distanceTolerance;
  }

  public Coordinate[] simplify()
  {
    usePt = new boolean[pts.length];
    for (int i = 0; i < pts.length; i++) {
      usePt[i] = true;
    }
    simplifySection(0, pts.length - 1);
    CoordinateList coordList = new CoordinateList();
    for (int i = 0; i < pts.length; i++) {
      if (usePt[i])
        coordList.add(new Coordinate(pts[i]));
    }
    return coordList.toCoordinateArray();
  }

  private LineSegment seg = new LineSegment();

  private void simplifySection(int i, int j)
  {
    if((i+1) == j) {
      return;
    }
    seg.p0 = pts[i];
    seg.p1 = pts[j];
    double maxDistance = -1.0;
    int maxIndex = i;
    for (int k = i + 1; k < j; k++) {
      double distance = seg.distance(pts[k]);
      if (distance > maxDistance) {
        maxDistance = distance;
        maxIndex = k;
      }
    }
    if (maxDistance <= distanceTolerance) {
      for(int k = i + 1; k < j; k++) {
        usePt[k] = false;
      }
    }
    else {
      simplifySection(i, maxIndex);
      simplifySection(maxIndex, j);
    }
  }

}
