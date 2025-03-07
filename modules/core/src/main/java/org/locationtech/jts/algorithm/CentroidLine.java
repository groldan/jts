
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
package org.locationtech.jts.algorithm;

import org.locationtech.jts.geom.*;

/**
 * Computes the centroid of a linear geometry.
 * <h2>Algorithm</h2>
 * Compute the average of the midpoints
 * of all line segments weighted by the segment length.
 *
 * @version 1.7
 * @deprecated use Centroid instead
 */
public class CentroidLine
{
  private Coordinate centSum = new Coordinate();
  private double totalLength = 0.0;

  public CentroidLine()
  {
  }

  /**
   * Adds the linear components of by a Geometry to the centroid total.
   * If the geometry has no linear components it does not contribute to the centroid,
   * 
   * @param geom the geometry to add
   */
  public void add(Geometry geom)
  {
    if (geom instanceof LineString) {
      add(geom.getCoordinates());
    }
    else if (geom instanceof Polygon) {
    	Polygon poly = (Polygon) geom;
    	// add linear components of a polygon
      add(poly.getExteriorRing().getCoordinates());
      for (int i = 0; i < poly.getNumInteriorRing(); i++) {
        add(poly.getInteriorRingN(i).getCoordinates());
      }
		}
    else if (geom instanceof GeometryCollection) {
      GeometryCollection gc = (GeometryCollection) geom;
      for (int i = 0; i < gc.getNumGeometries(); i++) {
        add(gc.getGeometryN(i));
      }
    }
  }

  public Coordinate getCentroid()
  {
    Coordinate cent = new Coordinate();
    cent.x = centSum.x / totalLength;
    cent.y = centSum.y / totalLength;
    return cent;
  }

  /**
   * Adds the length defined by an array of coordinates.
   * @param pts an array of {@link Coordinate}s
   */
  public void add(Coordinate[] pts)
  {
    for (int i = 0; i < pts.length - 1; i++) {
      double segmentLen = pts[i].distance(pts[i + 1]);
      totalLength += segmentLen;

      double midx = (pts[i].x + pts[i + 1].x) / 2;
      centSum.x += segmentLen * midx;
      double midy = (pts[i].y + pts[i + 1].y) / 2;
      centSum.y += segmentLen * midy;
    }
  }

}
