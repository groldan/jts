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

package org.locationtech.jts.algorithm.match;

import org.locationtech.jts.algorithm.distance.*;
import org.locationtech.jts.geom.*;

/**
 * Measures the degree of similarity between two {@link Geometry}s
 * using the Hausdorff distance metric.
 * The measure is normalized to lie in the range [0, 1].
 * Higher measures indicate a great degree of similarity.
 * <p>
 * The measure is computed by computing the Hausdorff distance
 * between the input geometries, and then normalizing
 * this by dividing it by the diagonal distance across 
 * the envelope of the combined geometries.
 * 
 * @author mbdavis
 *
 */
public class HausdorffSimilarityMeasure 
	implements SimilarityMeasure
{
	/*
	public static double measure(Geometry a, Geometry b)
	{
		HausdorffSimilarityMeasure gv = new HausdorffSimilarityMeasure(a, b);
		return gv.measure();
	}
	*/
	
	public HausdorffSimilarityMeasure()
	{
	}
	
	/*
	 * Densify a small amount to increase accuracy of Hausdorff distance
	 */
	private static final double DENSIFY_FRACTION = 0.25;
	
	public double measure(Geometry g1, Geometry g2)
	{		
		double distance = DiscreteHausdorffDistance.distance(g1, g2, DENSIFY_FRACTION);
		
		Envelope env = new Envelope(g1.getEnvelopeInternal());
		env.expandToInclude(g2.getEnvelopeInternal());
		double envSize = diagonalSize(env);
		// normalize so that more similarity produces a measure closer to 1
		double measure = 1 - distance / envSize;
		
		//System.out.println("Hausdorff distance = " + distance + ", measure = " + measure);
		return measure;
	}
	
	public static double diagonalSize(Envelope env)
	{
		if (env.isNull()) return 0.0;
		
		double width = env.getWidth(); 
		double hgt = env.getHeight();
		return Math.sqrt(width * width + hgt * hgt);
	}
}
