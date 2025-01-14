/*
 * The JTS Topology Suite is a collection of Java classes that
 * implement the fundamental operations required to validate a given
 * geo-spatial data set to a known topological specification.
 * 
 * Copyright (C) 2016 Martin Davis
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Martin Davis BSD
 * License v1.0 (found at the root of the repository).
 * 
 */
package org.locationtech.jts.algorithm;

import org.locationtech.jts.geom.*;

/**
 * Computes whether a rectangle intersects line segments.
 * <p>
 * Rectangles contain a large amount of inherent symmetry
 * (or to put it another way, although they contain four
 * coordinates they only actually contain 4 ordinates
 * worth of information).
 * The algorithm used takes advantage of the symmetry of 
 * the geometric situation 
 * to optimize performance by minimizing the number
 * of line intersection tests.
 * 
 * @author Martin Davis
 *
 */
public class RectangleLineIntersector
{
  // for intersection testing, don't need to set precision model
  private LineIntersector li = new RobustLineIntersector();

  private Envelope rectEnv;
  
  private Coordinate diagUp0;
  private Coordinate diagUp1;
  private Coordinate diagDown0;
  private Coordinate diagDown1;
  
  /**
   * Creates a new intersector for the given query rectangle,
   * specified as an {@link Envelope}.
   * 
   * 
   * @param rectEnv the query rectangle, specified as an Envelope
   */
  public RectangleLineIntersector(Envelope rectEnv)
  {
    this.rectEnv = rectEnv;
    
    /**
     * Up and Down are the diagonal orientations
     * relative to the Left side of the rectangle.
     * Index 0 is the left side, 1 is the right side.
     */
    diagUp0 = new Coordinate(rectEnv.getMinX(), rectEnv.getMinY());
    diagUp1 = new Coordinate(rectEnv.getMaxX(), rectEnv.getMaxY());
    diagDown0 = new Coordinate(rectEnv.getMinX(), rectEnv.getMaxY());
    diagDown1 = new Coordinate(rectEnv.getMaxX(), rectEnv.getMinY());
  }
  
  /**
   * Tests whether the query rectangle intersects a 
   * given line segment.
   * 
   * @param p0 the first endpoint of the segment
   * @param p1 the second endpoint of the segment
   * @return true if the rectangle intersects the segment
   */
  public boolean intersects(Coordinate p0, Coordinate p1)
  {
    // TODO: confirm that checking envelopes first is faster

    /**
     * If the segment envelope is disjoint from the
     * rectangle envelope, there is no intersection
     */
    Envelope segEnv = new Envelope(p0, p1);
    if (! rectEnv.intersects(segEnv))
      return false;
    
    /**
     * If either segment endpoint lies in the rectangle,
     * there is an intersection.
     */
    if (rectEnv.intersects(p0)) return true;
    if (rectEnv.intersects(p1)) return true;
    
    /**
     * Normalize segment.
     * This makes p0 less than p1,
     * so that the segment runs to the right,
     * or vertically upwards.
     */
    if (p0.compareTo(p1) > 0) {
      Coordinate tmp = p0;
      p0 = p1;
      p1 = tmp;
    }
    /**
     * Compute angle of segment.
     * Since the segment is normalized to run left to right,
     * it is sufficient to simply test the Y ordinate.
     * "Upwards" means relative to the left end of the segment.
     */
    boolean isSegUpwards = false;
    if (p1.y > p0.y)
      isSegUpwards = true;
    
    /**
     * Since we now know that neither segment endpoint
     * lies in the rectangle, there are two possible 
     * situations:
     * 1) the segment is disjoint to the rectangle
     * 2) the segment crosses the rectangle completely.
     * 
     * In the case of a crossing, the segment must intersect 
     * a diagonal of the rectangle.
     * 
     * To distinguish these two cases, it is sufficient 
     * to test intersection with 
     * a single diagonal of the rectangle,
     * namely the one with slope "opposite" to the slope
     * of the segment.
     * (Note that if the segment is axis-parallel,
     * it must intersect both diagonals, so this is
     * still sufficient.)  
     */
    if (isSegUpwards) {
      li.computeIntersection(p0, p1, diagDown0, diagDown1);
    }
    else {
      li.computeIntersection(p0, p1, diagUp0, diagUp1);      
    }
    if (li.hasIntersection())
      return true;
    return false;

      
  }
}
