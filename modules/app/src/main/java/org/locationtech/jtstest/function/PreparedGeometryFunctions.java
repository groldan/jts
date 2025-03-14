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

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;

public class PreparedGeometryFunctions 
{
  private static PreparedGeometry createPG(Geometry g)
  {
    return (new PreparedGeometryFactory()).create(g);
  }
  
  public static boolean preparedIntersects(Geometry g1, Geometry g2)
  {
    return createPG(g1).intersects(g2);
  }
  
  public static boolean intersects(Geometry g1, Geometry g2)
  {
    return g1.intersects(g2);
  }
  

}
