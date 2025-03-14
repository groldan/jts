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

package org.locationtech.jts.linearref;

import org.locationtech.jts.geom.*;

/**
 * Determines the location of a subline along a linear {@link Geometry}.
 * The location is reported as a pair of {@link LinearLocation}s.
 * <p>
 * <b>Note:</b> Currently this algorithm is not guaranteed to
 * return the correct substring in some situations where
 * an endpoint of the test line occurs more than once in the input line.
 * (However, the common case of a ring is always handled correctly).
 */
class LocationIndexOfLine
{
  /**
  * MD - this algorithm has been extracted into a class
  * because it is intended to validate that the subline truly is a subline,
  * and also to use the internal vertex information to unambiguously locate the subline.
  */
 public static LinearLocation[] indicesOf(Geometry linearGeom, Geometry subLine)
  {
    LocationIndexOfLine locater = new LocationIndexOfLine(linearGeom);
    return locater.indicesOf(subLine);
  }

  private Geometry linearGeom;

  public LocationIndexOfLine(Geometry linearGeom) {
    this.linearGeom = linearGeom;
  }

  public LinearLocation[] indicesOf(Geometry subLine)
  {
    Coordinate startPt = ((LineString) subLine.getGeometryN(0)).getCoordinateN(0);
    LineString lastLine = (LineString) subLine.getGeometryN(subLine.getNumGeometries() - 1);
    Coordinate endPt = lastLine.getCoordinateN(lastLine.getNumPoints() - 1);

    LocationIndexOfPoint locPt = new LocationIndexOfPoint(linearGeom);
    LinearLocation[] subLineLoc = new LinearLocation[2];
    subLineLoc[0] = locPt.indexOf(startPt);

    // check for case where subline is zero length
    if (subLine.getLength() == 0.0) {
      subLineLoc[1] = (LinearLocation) subLineLoc[0].clone();
    }
    else  {
      subLineLoc[1] = locPt.indexOfAfter(endPt, subLineLoc[0]);
    }
    return subLineLoc;
  }
}
