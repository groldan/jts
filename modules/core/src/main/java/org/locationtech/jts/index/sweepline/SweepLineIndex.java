

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
package org.locationtech.jts.index.sweepline;

import java.util.*;

/**
 * A sweepline implements a sorted index on a set of intervals.
 * It is used to compute all overlaps between the interval in the index.
 *
 * @version 1.7
 */
public class SweepLineIndex {

  List events = new ArrayList();
  private boolean indexBuilt;
  // statistics information
  private int nOverlaps;

  public SweepLineIndex() {
  }

  public void add(SweepLineInterval sweepInt)
  {
    SweepLineEvent insertEvent = new SweepLineEvent(sweepInt.getMin(), null, sweepInt);
    events.add(insertEvent);
    events.add(new SweepLineEvent(sweepInt.getMax(), insertEvent, sweepInt));
  }

  /**
   * Because Delete Events have a link to their corresponding Insert event,
   * it is possible to compute exactly the range of events which must be
   * compared to a given Insert event object.
   */
  private void buildIndex()
  {
    if (indexBuilt) return;
    Collections.sort(events);
    for (int i = 0; i < events.size(); i++ )
    {
      SweepLineEvent ev = (SweepLineEvent) events.get(i);
      if (ev.isDelete()) {
        ev.getInsertEvent().setDeleteEventIndex(i);
      }
    }
    indexBuilt = true;
  }

  public void computeOverlaps(SweepLineOverlapAction action)
  {
    nOverlaps = 0;
    buildIndex();

    for (int i = 0; i < events.size(); i++ )
    {
      SweepLineEvent ev = (SweepLineEvent) events.get(i);
      if (ev.isInsert()) {
        processOverlaps(i, ev.getDeleteEventIndex(), ev.getInterval(), action);
      }
    }
  }

  private void processOverlaps(int start, int end, SweepLineInterval s0, SweepLineOverlapAction action)
  {
    /**
     * Since we might need to test for self-intersections,
     * include current insert event object in list of event objects to test.
     * Last index can be skipped, because it must be a Delete event.
     */
    for (int i = start; i < end; i++ ) {
      SweepLineEvent ev = (SweepLineEvent) events.get(i);
      if (ev.isInsert()) {
        SweepLineInterval s1 = ev.getInterval();
        action.overlap(s0, s1);
        nOverlaps++;
      }
    }

  }


}
