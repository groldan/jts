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
package org.locationtech.jts.operation;

import java.util.*;

import org.locationtech.jts.algorithm.BoundaryNodeRule;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geomgraph.GeometryGraph;

/**
 * Computes the boundary of a {@link Geometry}.
 * Allows specifying the {@link BoundaryNodeRule} to be used.
 * This operation will always return a {@link Geometry} of the appropriate
 * dimension for the boundary (even if the input geometry is empty).
 * The boundary of zero-dimensional geometries (Points) is
 * always the empty {@link GeometryCollection}.
 *
 * @author Martin Davis
 * @version 1.7
 */

public class BoundaryOp
{
  public static Geometry getBoundary(Geometry g)
  {
    BoundaryOp bop = new BoundaryOp(g);
    return bop.getBoundary();
  }
  
  public static Geometry getBoundary(Geometry g, BoundaryNodeRule bnRule)
  {
    BoundaryOp bop = new BoundaryOp(g, bnRule);
    return bop.getBoundary();
  }
  
  private Geometry geom;
  private GeometryFactory geomFact;
  private BoundaryNodeRule bnRule;

  public BoundaryOp(Geometry geom)
  {
    this(geom, BoundaryNodeRule.MOD2_BOUNDARY_RULE);
  }

  public BoundaryOp(Geometry geom, BoundaryNodeRule bnRule)
  {
    this.geom = geom;
    geomFact = geom.getFactory();
    this.bnRule = bnRule;
  }

  public Geometry getBoundary()
  {
    if (geom instanceof LineString) return boundaryLineString((LineString) geom);
    if (geom instanceof MultiLineString) return boundaryMultiLineString((MultiLineString) geom);
    return geom.getBoundary();
  }

  private MultiPoint getEmptyMultiPoint()
  {
    return geomFact.createMultiPoint((CoordinateSequence) null);
  }

  private Geometry boundaryMultiLineString(MultiLineString mLine)
  {
    if (geom.isEmpty()) {
      return getEmptyMultiPoint();
    }

    Coordinate[] bdyPts = computeBoundaryCoordinates(mLine);

    // return Point or MultiPoint
    if (bdyPts.length == 1) {
      return geomFact.createPoint(bdyPts[0]);
    }
    // this handles 0 points case as well
    return geomFact.createMultiPoint(bdyPts);
  }

/*
// MD - superseded
  private Coordinate[] computeBoundaryFromGeometryGraph(MultiLineString mLine)
  {
    GeometryGraph g = new GeometryGraph(0, mLine, bnRule);
    Coordinate[] bdyPts = g.getBoundaryPoints();
    return bdyPts;
  }
*/

  private Map endpointMap;

  private Coordinate[] computeBoundaryCoordinates(MultiLineString mLine)
  {
    List bdyPts = new ArrayList();
    endpointMap = new TreeMap();
    for (int i = 0; i < mLine.getNumGeometries(); i++) {
      LineString line = (LineString) mLine.getGeometryN(i);
      if (line.getNumPoints() == 0)
        continue;
      addEndpoint(line.getCoordinateN(0));
      addEndpoint(line.getCoordinateN(line.getNumPoints() - 1));
    }

    for (Iterator it = endpointMap.entrySet().iterator(); it.hasNext(); ) {
      Map.Entry entry = (Map.Entry) it.next();
      Counter counter = (Counter) entry.getValue();
      int valence = counter.count;
      if (bnRule.isInBoundary(valence)) {
        bdyPts.add(entry.getKey());
      }
    }

    return CoordinateArrays.toCoordinateArray(bdyPts);
  }

  private void addEndpoint(Coordinate pt)
  {
    Counter counter = (Counter) endpointMap.get(pt);
    if (counter == null) {
      counter = new Counter();
      endpointMap.put(pt, counter);
    }
    counter.count++;
  }

  private Geometry boundaryLineString(LineString line)
  {
    if (geom.isEmpty()) {
      return getEmptyMultiPoint();
    }

    if (line.isClosed()) {
      // check whether endpoints of valence 2 are on the boundary or not
      boolean closedEndpointOnBoundary = bnRule.isInBoundary(2);
      if (closedEndpointOnBoundary) {
        return line.getStartPoint();
      }
      else {
        return geomFact.createMultiPoint((Coordinate[]) null);
      }
    }
    return geomFact.createMultiPoint(new Point[]{
                                     line.getStartPoint(),
                                     line.getEndPoint()
    });
  }
}

/**
 * Stores an integer count, for use as a Map entry.
 *
 * @author Martin Davis
 * @version 1.7
 */
class Counter
{
  /**
   * The value of the count
   */
  int count;
}
