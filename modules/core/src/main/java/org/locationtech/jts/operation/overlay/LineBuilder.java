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
package org.locationtech.jts.operation.overlay;

import java.util.*;

import org.locationtech.jts.algorithm.*;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geomgraph.*;
import org.locationtech.jts.util.*;

/**
 * Forms JTS LineStrings out of a the graph of {@link DirectedEdge}s
 * created by an {@link OverlayOp}.
 *
 * @version 1.7
 */
public class LineBuilder {
  private OverlayOp op;
  private GeometryFactory geometryFactory;
  private PointLocator ptLocator;

  private List lineEdgesList    = new ArrayList();
  private List resultLineList   = new ArrayList();

  public LineBuilder(OverlayOp op, GeometryFactory geometryFactory, PointLocator ptLocator) {
    this.op = op;
    this.geometryFactory = geometryFactory;
    this.ptLocator = ptLocator;
  }
  /**
   * @return a list of the LineStrings in the result of the specified overlay operation
   */
  public List build(int opCode)
  {
    findCoveredLineEdges();
    collectLines(opCode);
    //labelIsolatedLines(lineEdgesList);
    buildLines(opCode);
    return resultLineList;
  }
  /**
   * Find and mark L edges which are "covered" by the result area (if any).
   * L edges at nodes which also have A edges can be checked by checking
   * their depth at that node.
   * L edges at nodes which do not have A edges can be checked by doing a
   * point-in-polygon test with the previously computed result areas.
   */
  private void findCoveredLineEdges()
  {
    // first set covered for all L edges at nodes which have A edges too
    for (Iterator nodeit = op.getGraph().getNodes().iterator(); nodeit.hasNext(); ) {
      Node node = (Node) nodeit.next();
//node.print(System.out);
      ((DirectedEdgeStar) node.getEdges()).findCoveredLineEdges();
    }

    /**
     * For all L edges which weren't handled by the above,
     * use a point-in-poly test to determine whether they are covered
     */
    for (Iterator it = op.getGraph().getEdgeEnds().iterator(); it.hasNext(); ) {
      DirectedEdge de = (DirectedEdge) it.next();
      Edge e = de.getEdge();
      if (de.isLineEdge() && ! e.isCoveredSet()) {
        boolean isCovered = op.isCoveredByA(de.getCoordinate());
        e.setCovered(isCovered);
      }
    }
  }

  private void collectLines(int opCode)
  {
    for (Iterator it = op.getGraph().getEdgeEnds().iterator(); it.hasNext(); ) {
      DirectedEdge de = (DirectedEdge) it.next();
      collectLineEdge(de, opCode, lineEdgesList);
      collectBoundaryTouchEdge(de, opCode, lineEdgesList);
    }
  }

  /**
   * Collect line edges which are in the result.
   * Line edges are in the result if they are not part of
   * an area boundary, if they are in the result of the overlay operation,
   * and if they are not covered by a result area.
   *
   * @param de the directed edge to test
   * @param opCode the overlap operation
   * @param edges the list of included line edges
   */
  private void collectLineEdge(DirectedEdge de, int opCode, List edges)
  {
    Label label = de.getLabel();
    Edge e = de.getEdge();
    // include L edges which are in the result
    if (de.isLineEdge()) {
      if (! de.isVisited() && OverlayOp.isResultOfOp(label, opCode) && ! e.isCovered()) {
//Debug.println("de: " + de.getLabel());
//Debug.println("edge: " + e.getLabel());

        edges.add(e);
        de.setVisitedEdge(true);
      }
    }
  }

  /**
   * Collect edges from Area inputs which should be in the result but
   * which have not been included in a result area.
   * This happens ONLY:
   * <ul>
   * <li>during an intersection when the boundaries of two
   * areas touch in a line segment
   * <li> OR as a result of a dimensional collapse.
   * </ul>
   */
  private void collectBoundaryTouchEdge(DirectedEdge de, int opCode, List edges)
  {
    Label label = de.getLabel();
    if (de.isLineEdge()) return;  // only interested in area edges
    if (de.isVisited()) return;  // already processed
    if (de.isInteriorAreaEdge()) return;  // added to handle dimensional collapses
    if (de.getEdge().isInResult()) return;  // if the edge linework is already included, don't include it again

    // sanity check for labelling of result edgerings
    Assert.isTrue(! (de.isInResult() || de.getSym().isInResult()) || ! de.getEdge().isInResult());

    // include the linework if it's in the result of the operation
    if (OverlayOp.isResultOfOp(label, opCode)
          && opCode == OverlayOp.INTERSECTION)
    {
      edges.add(de.getEdge());
      de.setVisitedEdge(true);
    }
  }

  private void buildLines(int opCode)
  {
    for (Iterator it = lineEdgesList.iterator(); it.hasNext(); ) {
      Edge e = (Edge) it.next();
      Label label = e.getLabel();
        LineString line = geometryFactory.createLineString(e.getCoordinates());
        resultLineList.add(line);
        e.setInResult(true);
    }
  }

  private void labelIsolatedLines(List edgesList)
  {
    for (Iterator it = edgesList.iterator(); it.hasNext(); ) {
      Edge e = (Edge) it.next();
      Label label = e.getLabel();
//n.print(System.out);
      if (e.isIsolated()) {
        if (label.isNull(0))
          labelIsolatedLine(e, 0);
        else
          labelIsolatedLine(e, 1);
      }
    }
  }
  /**
   * Label an isolated node with its relationship to the target geometry.
   */
  private void labelIsolatedLine(Edge e, int targetIndex)
  {
    int loc = ptLocator.locate(e.getCoordinate(), op.getArgGeometry(targetIndex));
    e.getLabel().setLocation(targetIndex, loc);
  }


}
