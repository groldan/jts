
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
package org.locationtech.jts.index.strtree;

import java.io.Serializable;
import java.util.*;

import org.locationtech.jts.util.*;

/**
 * A node of an {@link AbstractSTRtree}. A node is one of:
 * <ul>
 * <li>empty
 * <li>an <i>interior node</i> containing child {@link AbstractNode}s
 * <li>a <i>leaf node</i> containing data items ({@link ItemBoundable}s). 
 * </ul>
 * A node stores the bounds of its children, and its level within the index tree.
 *
 * @version 1.7
 */
public abstract class AbstractNode implements Boundable, Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 6493722185909573708L;
  
  private ArrayList childBoundables = new ArrayList();
  private Object bounds = null;
  private int level;

  /**
   * Default constructor required for serialization.
   */
  public AbstractNode() {
  }

  /**
   * Constructs an AbstractNode at the given level in the tree
   * @param level 0 if this node is a leaf, 1 if a parent of a leaf, and so on; the
   * root node will have the highest level
   */
  public AbstractNode(int level) {
    this.level = level;
  }

  /**
   * Returns either child {@link AbstractNode}s, or if this is a leaf node, real data (wrapped
   * in {@link ItemBoundable}s).
   */
  public List getChildBoundables() {
    return childBoundables;
  }

  /**
   * Returns a representation of space that encloses this Boundable,
   * preferably not much bigger than this Boundable's boundary yet fast to
   * test for intersection with the bounds of other Boundables. The class of
   * object returned depends on the subclass of AbstractSTRtree.
   *
   * @return an Envelope (for STRtrees), an Interval (for SIRtrees), or other
   *         object (for other subclasses of AbstractSTRtree)
   * @see AbstractSTRtree.IntersectsOp
   */
  protected abstract Object computeBounds();

  /**
   * Gets the bounds of this node
   * 
   * @return the object representing bounds in this index
   */
  public Object getBounds() {
    if (bounds == null) {
      bounds = computeBounds();
    }
    return bounds;
  }

  /**
   * Returns 0 if this node is a leaf, 1 if a parent of a leaf, and so on; the
   * root node will have the highest level
   */
  public int getLevel() {
    return level;
  }

  /**
   * Gets the count of the {@link Boundable}s at this node.
   * 
   * @return the count of boundables at this node
   */
  public int size()
  {
    return childBoundables.size();
  }
  
  /**
   * Tests whether there are any {@link Boundable}s at this node.
   * 
   * @return true if there are boundables at this node
   */
  public boolean isEmpty()
  {
    return childBoundables.isEmpty();
  }
  
  /**
   * Adds either an AbstractNode, or if this is a leaf node, a data object
   * (wrapped in an ItemBoundable)
   */
  public void addChildBoundable(Boundable childBoundable) {
    Assert.isTrue(bounds == null);
    childBoundables.add(childBoundable);
  }
}
