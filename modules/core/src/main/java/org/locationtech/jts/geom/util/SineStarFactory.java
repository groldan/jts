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

package org.locationtech.jts.geom.util;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.util.*;

/**
 * Creates geometries which are shaped like multi-armed stars
 * with each arm shaped like a sine wave.
 * These kinds of geometries are useful as a more complex 
 * geometry for testing algorithms.
 * 
 * @author Martin Davis
 *
 */
public class SineStarFactory
	extends GeometricShapeFactory
{
	protected int numArms = 8;
	protected double armLengthRatio = 0.5;
	
  /**
   * Creates a factory which will create sine stars using the default
   * {@link GeometryFactory}.
   *
   * @param geomFact the factory to use
   */
	public SineStarFactory()
	{
		super();
	}
	
  /**
   * Creates a factory which will create sine stars using the given
   * {@link GeometryFactory}.
   *
   * @param geomFact the factory to use
   */
  public SineStarFactory(GeometryFactory geomFact)
  {
    super(geomFact);
  }

  /**
   * Sets the number of arms in the star
   * 
   * @param numArms the number of arms to generate
   */
  public void setNumArms(int numArms)
  {
  	this.numArms = numArms;
  }
  
  /**
   * Sets the ration of the length of each arm to the distance from the tip
   * of the arm to the centre of the star.
   * Value should be between 0.0 and 1.0
   * 
   * @param armLengthRatio
   */
  public void setArmLengthRatio(double armLengthRatio)
  {
  	this.armLengthRatio = armLengthRatio;
  }
  
  /**
   * Generates the geometry for the sine star
   * 
   * @return the geometry representing the sine star
   */
  public Geometry createSineStar()
  {
    Envelope env = dim.getEnvelope();
    double radius = env.getWidth() / 2.0;

  	double armRatio = armLengthRatio;
    if (armRatio < 0.0)
      armRatio = 0.0;
    if (armRatio > 1.0)
      armRatio = 1.0;

    double armMaxLen = armRatio * radius;
    double insideRadius = (1 - armRatio) * radius;

    double centreX = env.getMinX() + radius;
    double centreY = env.getMinY() + radius;

    Coordinate[] pts = new Coordinate[nPts + 1];
    int iPt = 0;
    for (int i = 0; i < nPts; i++) {
      // the fraction of the way thru the current arm - in [0,1]
      double ptArcFrac = (i / (double) nPts) * numArms;
      double armAngFrac = ptArcFrac - Math.floor(ptArcFrac);
      
      // the angle for the current arm - in [0,2Pi]  
      // (each arm is a complete sine wave cycle)
      double armAng = 2 * Math.PI * armAngFrac;
      // the current length of the arm
      double armLenFrac = (Math.cos(armAng) + 1.0) / 2.0;
      
      // the current radius of the curve (core + arm)
      double curveRadius = insideRadius + armMaxLen * armLenFrac;

      // the current angle of the curve
      double ang = i * (2 * Math.PI / nPts);
      double x = curveRadius * Math.cos(ang) + centreX;
      double y = curveRadius * Math.sin(ang) + centreY;
      pts[iPt++] = coord(x, y);
    }
    pts[iPt] = new Coordinate(pts[0]);

    LinearRing ring = geomFact.createLinearRing(pts);
    Polygon poly = geomFact.createPolygon(ring, null);
    return poly;
  }
}
