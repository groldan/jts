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
package org.locationtech.jtstest.function;

import org.locationtech.jts.densify.*;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.operation.buffer.BufferOp;
import org.locationtech.jts.operation.buffer.BufferParameters;
import org.locationtech.jts.precision.MinimumClearance;

/**
 * Geometry functions which
 * augment the existing methods on {@link Geometry},
 * for use in XML Test files.
 * This is the default used in the TestRunner, 
 * and thus all the operations 
 * in this class should be named differently to the Geometry methods
 * (otherwise they will shadow the real Geometry methods).
 * <p>
 * If replacing a Geometry method is desired, this
 * can be done via the -geomfunc argument to the TestRunner.
 * 
 * @author Martin Davis
 *
 */
public class TestCaseGeometryFunctions 
{
	public static Geometry bufferMitredJoin(Geometry g, double distance)	
	{
    BufferParameters bufParams = new BufferParameters();
    bufParams.setJoinStyle(BufferParameters.JOIN_MITRE);
    
    return BufferOp.bufferOp(g, distance, bufParams);
	}

  public static Geometry densify(Geometry g, double distance) 
  {
    return Densifier.densify(g, distance);
  }

  public static double minClearance(Geometry g) 
  {
    return MinimumClearance.getDistance(g);
  }

  public static Geometry minClearanceLine(Geometry g) 
  {
    return MinimumClearance.getLine(g);
  }

}
