


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
package org.locationtech.jts.geomgraph;

/**
 * A Position indicates the position of a Location relative to a graph component
 * (Node, Edge, or Area).
 * @version 1.7
 */
public class Position {

  /** An indicator that a Location is <i>on</i> a GraphComponent */
  public static final int ON      = 0;
  /** An indicator that a Location is to the <i>left</i> of a GraphComponent */  
  public static final int LEFT    = 1;
  /** An indicator that a Location is to the <i>right</i> of a GraphComponent */  
  public static final int RIGHT   = 2;
  /**
   * Returns LEFT if the position is RIGHT, RIGHT if the position is LEFT, or the position
   * otherwise.
   */
  public static final int opposite(int position)
  {
    if (position == LEFT) return RIGHT;
    if (position == RIGHT) return LEFT;
    return position;
  }
}
