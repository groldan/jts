
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
package org.locationtech.jtsexample.operation.polygonize;

import java.util.ArrayList;
import java.util.Collection;

import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.operation.polygonize.Polygonizer;

/**
 *  Example of using Polygonizer class to polygonize a set of fully noded linestrings
 *
 * @version 1.7
 */
public class PolygonizeExample
{
  public static void main(String[] args) throws Exception
  {
    PolygonizeExample test = new PolygonizeExample();
    try {
      test.run();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  public PolygonizeExample() {
  }

  void run()
      throws Exception
  {
    WKTReader rdr = new WKTReader();
    Collection lines = new ArrayList();

    lines.add(rdr.read("LINESTRING (0 0 , 10 10)"));   // isolated edge
    lines.add(rdr.read("LINESTRING (185 221, 100 100)"));   //dangling edge
    lines.add(rdr.read("LINESTRING (185 221, 88 275, 180 316)"));
    lines.add(rdr.read("LINESTRING (185 221, 292 281, 180 316)"));
    lines.add(rdr.read("LINESTRING (189 98, 83 187, 185 221)"));
    lines.add(rdr.read("LINESTRING (189 98, 325 168, 185 221)"));

    Polygonizer polygonizer = new Polygonizer();
    polygonizer.add(lines);

    Collection polys = polygonizer.getPolygons();

    System.out.println("Polygons formed (" + polys.size() + "):");
    System.out.println(polys);
  }

}
