/*
 * The JTS Topology Suite is a collection of Java classes that
 * implement the fundamental operations required to validate a given
 * geo-spatial data set to a known topological specification.
 * 
 * Copyright (C) 2016 Martin Davis
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Martin Davis BSD
 * License v1.0 (found at the root of the repository).
 * 
 */

package org.locationtech.jts.math;

import org.locationtech.jts.math.DD;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * Various tests involving computing known mathematical quantities
 * using the basic {@link DD} arithmetic operations.
 * 
 * @author Martin Davis
 *
 */
public class DDComputeTest 
  extends TestCase
{
	public static void main(String args[]) {
        TestRunner.run(DDComputeTest.class);
      }

	public DDComputeTest(String name) { super(name); }

	public void testEByTaylorSeries()
	{
    //System.out.println("--------------------------------");
    //System.out.println("Computing e by Taylor series");
		DD testE = computeEByTaylorSeries();
		double err = Math.abs(testE.subtract(DD.E).doubleValue());
		//System.out.println("Difference from DoubleDouble.E = " + err);
		assertTrue(err < 64 * DD.EPS);
	}
	
  /**
	 * Uses Taylor series to compute e
	 * 
	 * e = 1 + 1 + 1/2! + 1/3! + 1/4! + ...
	 * 
	 * @return an approximation to e
	 */
	private DD computeEByTaylorSeries()
	{
		DD s = DD.valueOf(2.0);
		DD t = DD.valueOf(1.0);
		double n = 1.0;
		int i = 0;

		while (t.doubleValue() > DD.EPS) {
			i++;
			n += 1.0;
			t = t.divide(DD.valueOf(n));
			s = s.add(t);
			//System.out.println(i + ": " + s);
		}
		return s;
	}
	
	public void testPiByMachin()
	{
    //System.out.println("--------------------------------");
    //System.out.println("Computing Pi by Machin's rule");
		DD testE = computePiByMachin();
		double err = Math.abs(testE.subtract(DD.PI).doubleValue());
		//System.out.println("Difference from DoubleDouble.PI = " + err);
		assertTrue(err < 8 * DD.EPS);
	}
	

	/**
	 * Uses Machin's arctangent formula to compute Pi:
	 *
   *    Pi / 4  =  4 arctan(1/5) - arctan(1/239)
   *    
	 * @return an approximation to Pi
	 */
	private DD computePiByMachin()
	{
		DD t1 = DD.valueOf(1.0).divide(DD.valueOf(5.0));
		DD t2 = DD.valueOf(1.0).divide(DD.valueOf(239.0));
		
		DD pi4 = (DD.valueOf(4.0)
												.multiply(arctan(t1)))
												.subtract(arctan(t2));
		DD pi = DD.valueOf(4.0).multiply(pi4);
		//System.out.println("Computed value = " + pi);
		return pi;
	}
	
	/**
	 * Computes the arctangent based on the Taylor series expansion
   *
   *    arctan(x) = x - x^3 / 3 + x^5 / 5 - x^7 / 7 + ...
   *    
	 * @param x the argument
	 * @return an approximation to the arctangent of the input
	 */
	private DD arctan(DD x)
	{
		DD t = x;
	  DD t2 = t.sqr();
	  DD at = new DD(0.0);
	  DD two = new DD(2.0);
	  int k = 0;
	  DD d = new DD(1.0);
	  int sign = 1;
	  while (t.doubleValue() > DD.EPS) {
	    k++;
	    if (sign < 0)
	      at = at.subtract(t.divide(d));
	    else
	    	at = at.add(t.divide(d));

	    d = d.add(two);
	    t = t.multiply(t2);
	    sign = -sign;
	  }
	  //System.out.println("Computed DD.atan(): " + at 
	  //		+ "    Math.atan = " + Math.atan(x.doubleValue()));
	  return at;
	}

}