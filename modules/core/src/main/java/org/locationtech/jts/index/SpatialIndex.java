
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
package org.locationtech.jts.index;

import java.util.*;

import org.locationtech.jts.geom.Envelope;

/**
 * The basic operations supported by classes
 * implementing spatial index algorithms.
 * <p>
 * A spatial index typically provides a primary filter for range rectangle queries.
 * A secondary filter is required to test for exact intersection.
 * The secondary filter may consist of other kinds of tests,
 * such as testing other spatial relationships.
 *
 * @version 1.7
 */
public interface SpatialIndex
{
  /**
   * Adds a spatial item with an extent specified by the given {@link Envelope} to the index
   */
  void insert(Envelope itemEnv, Object item);

  /**
   * Queries the index for all items whose extents intersect the given search {@link Envelope}
   * Note that some kinds of indexes may also return objects which do not in fact
   * intersect the query envelope.
   *
   * @param searchEnv the envelope to query for
   * @return a list of the items found by the query
   */
  List query(Envelope searchEnv);

  /**
   * Queries the index for all items whose extents intersect the given search {@link Envelope},
   * and applies an {@link ItemVisitor} to them.
   * Note that some kinds of indexes may also return objects which do not in fact
   * intersect the query envelope.
   *
   * @param searchEnv the envelope to query for
   * @param visitor a visitor object to apply to the items found
   */
  void query(Envelope searchEnv, ItemVisitor visitor);

  /**
   * Removes a single item from the tree.
   *
   * @param itemEnv the Envelope of the item to remove
   * @param item the item to remove
   * @return <code>true</code> if the item was found
   */
  boolean remove(Envelope itemEnv, Object item);

}
