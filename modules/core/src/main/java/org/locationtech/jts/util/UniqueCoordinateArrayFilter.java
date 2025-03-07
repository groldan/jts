

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
package org.locationtech.jts.util;

import java.util.ArrayList;
import java.util.TreeSet;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateFilter;


/**
 *  A {@link CoordinateFilter} that builds a set of <code>Coordinate</code>s.
 *  The set of coordinates contains no duplicate points.
 *  It preserves the order of the input points.
 *
 *@version 1.7
 */
public class UniqueCoordinateArrayFilter implements CoordinateFilter 
{
  /**
   * Convenience method which allows running the filter over an array of {@link Coordinate}s.
   * 
   * @param coords an array of coordinates
   * @return an array of the unique coordinates
   */
  public static Coordinate[] filterCoordinates(Coordinate[] coords)
  {
    UniqueCoordinateArrayFilter filter = new UniqueCoordinateArrayFilter();
    for (int i = 0; i < coords.length; i++) {
      filter.filter(coords[i]);
    }
    return filter.getCoordinates();
  }
  
  TreeSet treeSet = new TreeSet();
  ArrayList list = new ArrayList();

  public UniqueCoordinateArrayFilter() { }

  /**
   *  Returns the gathered <code>Coordinate</code>s.
   *
   *@return    the <code>Coordinate</code>s collected by this <code>CoordinateArrayFilter</code>
   */
  public Coordinate[] getCoordinates() {
    Coordinate[] coordinates = new Coordinate[list.size()];
    return (Coordinate[]) list.toArray(coordinates);
  }

  public void filter(Coordinate coord) {
    if (!treeSet.contains(coord)) {
      list.add(coord);
      treeSet.add(coord);
    }
  }
}

