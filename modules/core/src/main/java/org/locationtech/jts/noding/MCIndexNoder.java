
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
package org.locationtech.jts.noding;

import java.util.*;

import org.locationtech.jts.index.*;
import org.locationtech.jts.index.chain.*;
import org.locationtech.jts.index.strtree.*;

/**
 * Nodes a set of {@link SegmentString}s using a index based
 * on {@link MonotoneChain}s and a {@link SpatialIndex}.
 * The {@link SpatialIndex} used should be something that supports
 * envelope (range) queries efficiently (such as a <code>Quadtree</code>}
 * or {@link STRtree} (which is the default index provided).
 *
 * @version 1.7
 */
public class MCIndexNoder
    extends SinglePassNoder
{
  private List monoChains = new ArrayList();
  private SpatialIndex index= new STRtree();
  private int idCounter = 0;
  private Collection nodedSegStrings;
  // statistics
  private int nOverlaps = 0;

  public MCIndexNoder()
  {
  }
  public MCIndexNoder(SegmentIntersector si)
  {
    super(si);
  }

  public List getMonotoneChains() { return monoChains; }

  public SpatialIndex getIndex() { return index; }

  public Collection getNodedSubstrings()
  {
    return  NodedSegmentString.getNodedSubstrings(nodedSegStrings);
  }

  public void computeNodes(Collection inputSegStrings)
  {
    this.nodedSegStrings = inputSegStrings;
    for (Iterator i = inputSegStrings.iterator(); i.hasNext(); ) {
      add((SegmentString) i.next());
    }
    intersectChains();
//System.out.println("MCIndexNoder: # chain overlaps = " + nOverlaps);
  }

  private void intersectChains()
  {
    MonotoneChainOverlapAction overlapAction = new SegmentOverlapAction(segInt);

    for (Iterator i = monoChains.iterator(); i.hasNext(); ) {
      MonotoneChain queryChain = (MonotoneChain) i.next();
      List overlapChains = index.query(queryChain.getEnvelope());
      for (Iterator j = overlapChains.iterator(); j.hasNext(); ) {
        MonotoneChain testChain = (MonotoneChain) j.next();
        /**
         * following test makes sure we only compare each pair of chains once
         * and that we don't compare a chain to itself
         */
        if (testChain.getId() > queryChain.getId()) {
          queryChain.computeOverlaps(testChain, overlapAction);
          nOverlaps++;
        }
        // short-circuit if possible
        if (segInt.isDone())
        	return;
      }
    }
  }

  private void add(SegmentString segStr)
  {
    List segChains = MonotoneChainBuilder.getChains(segStr.getCoordinates(), segStr);
    for (Iterator i = segChains.iterator(); i.hasNext(); ) {
      MonotoneChain mc = (MonotoneChain) i.next();
      mc.setId(idCounter++);
      index.insert(mc.getEnvelope(), mc);
      monoChains.add(mc);
    }
  }

  public class SegmentOverlapAction
      extends MonotoneChainOverlapAction
  {
    private SegmentIntersector si = null;

    public SegmentOverlapAction(SegmentIntersector si)
    {
      this.si = si;
    }

    public void overlap(MonotoneChain mc1, int start1, MonotoneChain mc2, int start2)
    {
      SegmentString ss1 = (SegmentString) mc1.getContext();
      SegmentString ss2 = (SegmentString) mc2.getContext();
      si.processIntersections(ss1, start1, ss2, start2);
    }

  }
}
