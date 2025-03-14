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

import org.locationtech.jts.util.PriorityQueue;

import junit.framework.TestCase;

/**
 * @version 1.7
 */
public class PriorityQueueTest
    extends TestCase
{
  public PriorityQueueTest(String name) {
    super(name);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(PriorityQueueTest.class);
  }

  public void testOrder1() throws Exception {
    PriorityQueue q = new PriorityQueue();
    q.add(new Integer(1));
    q.add(new Integer(10));
    q.add(new Integer(5));
    q.add(new Integer(8));
    q.add(new Integer(-1));
    checkOrder(q);
  }
  
  public void testOrderRandom1() throws Exception {
    PriorityQueue q = new PriorityQueue();
    addRandomItems(q, 100);
    checkOrder(q);
  }
  
  private void addRandomItems(PriorityQueue q, int num)
  {
    for (int i = 0 ; i < num; i++) {
      q.add(new Integer((int) (num * Math.random())));
    }
  }
  
  private void checkOrder(PriorityQueue q)
  {
    Comparable curr = null;
    
    while (! q.isEmpty()) {
      Comparable next = (Comparable) q.poll();
      //System.out.println(next);
      if (curr == null)
        curr = next;
      else 
        assertTrue(next.compareTo(curr) >= 0);
    }
  }
}