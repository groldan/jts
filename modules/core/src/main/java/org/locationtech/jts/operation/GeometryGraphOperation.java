

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

import org.locationtech.jts.algorithm.*;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geomgraph.GeometryGraph;

/**
 * The base class for operations that require {@link GeometryGraph}s.
 *
 * @version 1.7
 */
public class GeometryGraphOperation
{
  protected final LineIntersector li = new RobustLineIntersector();
  protected PrecisionModel resultPrecisionModel;

  /**
   * The operation args into an array so they can be accessed by index
   */
  protected GeometryGraph[] arg;  // the arg(s) of the operation

  public GeometryGraphOperation(Geometry g0, Geometry g1)
  {
    this(g0, g1,
         BoundaryNodeRule.OGC_SFS_BOUNDARY_RULE
//         BoundaryNodeRule.ENDPOINT_BOUNDARY_RULE
         );
  }

  public GeometryGraphOperation(Geometry g0, Geometry g1, BoundaryNodeRule boundaryNodeRule)
  {
    // use the most precise model for the result
    if (g0.getPrecisionModel().compareTo(g1.getPrecisionModel()) >= 0)
      setComputationPrecision(g0.getPrecisionModel());
    else
      setComputationPrecision(g1.getPrecisionModel());

    arg = new GeometryGraph[2];
    arg[0] = new GeometryGraph(0, g0, boundaryNodeRule);
    arg[1] = new GeometryGraph(1, g1, boundaryNodeRule);
  }

  public GeometryGraphOperation(Geometry g0) {
    setComputationPrecision(g0.getPrecisionModel());

    arg = new GeometryGraph[1];
    arg[0] = new GeometryGraph(0, g0);;
  }

  public Geometry getArgGeometry(int i) { return arg[i].getGeometry(); }

  protected void setComputationPrecision(PrecisionModel pm)
  {
    resultPrecisionModel = pm;
    li.setPrecisionModel(resultPrecisionModel);
  }
}
