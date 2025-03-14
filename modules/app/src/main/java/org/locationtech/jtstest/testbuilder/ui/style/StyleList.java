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

package org.locationtech.jtstest.testbuilder.ui.style;

import java.util.*;
import java.awt.Graphics2D;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jtstest.testbuilder.ui.Viewport;
import org.locationtech.jtstest.testbuilder.ui.render.*;


/**
 * Contains a list of styles and allows Geometrys
 * to be rendered using those styles.
 * 
 * @author mbdavis
 *
 */
public class StyleList implements Style
{
	private List styleList = new ArrayList();
	
  public void paint(Geometry geom, Viewport viewport, Graphics2D g)
  	throws Exception
  {
  	for (Iterator i = styleList.iterator(); i.hasNext(); ) {
  		StyleEntry styleEntry = (StyleEntry) i.next();
  		if (styleEntry.isFullyEnabled())
  			styleEntry.getStyle().paint(geom, viewport, g);
  	}
  }

  public void add(Style style)
  {
  	add(style, null);
  }
  
  public void add(Style style, StyleFilter filter)
  {
  	styleList.add(new StyleEntry(style, filter));
  }
  
  public void setEnabled(Style style, boolean isEnabled)
  {
  	StyleEntry entry = getEntry(style);
  	if (entry == null)
  		return;
  	entry.setEnabled(isEnabled);
  }
  
  private StyleEntry getEntry(Style style)
  {
  	int index = getEntryIndex(style);
  	if (index < 0) return null;
  	return (StyleEntry) styleList.get(index);
  }
  
  private int getEntryIndex(Style style)
  {
  	for (int i = 0; i < styleList.size(); i++) {
  		StyleEntry entry = (StyleEntry) styleList.get(i);
  		if (entry.getStyle() == style)
  			return i;
  	}
  	return -1;
  }
  
  public interface StyleFilter {
  	boolean isFiltered(Style style);
  }
}

class StyleEntry
{
	private Style style;
	private boolean isEnabled = true;
	private StyleList.StyleFilter filter = null;
	
	public StyleEntry(Style style)
	{
		this.style = style;
	}
	
	public StyleEntry(Style style, StyleList.StyleFilter filter)
	{
		this.style = style;
		this.filter = filter;
	}
	
	public void setEnabled(boolean isEnabled)
	{
		this.isEnabled = isEnabled;
	}
	
	public boolean isEnabled() { return isEnabled; }
	
	public boolean isFiltered() {
		if (filter == null)
			return false;
		return filter.isFiltered(style);
	}
	
	public boolean isFullyEnabled()
	{
		return isEnabled() && ! isFiltered();
	}
	
	public Style getStyle() { return style; }
}