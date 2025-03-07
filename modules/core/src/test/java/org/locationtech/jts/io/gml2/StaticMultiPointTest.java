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
package org.locationtech.jts.io.gml2;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.locationtech.jts.generator.MultiGenerator;
import org.locationtech.jts.generator.PointGenerator;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.MultiPoint;
import org.xml.sax.SAXException;


/**
 * Round trip testing for GML reading and writing. 
 *
 * @author David Zwiers, Vivid Solutions. 
 */
public class StaticMultiPointTest extends WritingTestCase {

	/**
	 * @param arg
	 */
	public StaticMultiPointTest(String arg) {
		super(arg);
	}

	/**
	 * Round Trip test for a single MultiPoint
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public void testSingleMultiPointRoundTrip() throws SAXException, IOException, ParserConfigurationException{
		PointGenerator pgc = new PointGenerator();
		pgc.setGeometryFactory(geometryFactory);
		MultiGenerator pg = new MultiGenerator(pgc);
		pg.setBoundingBox(new Envelope(0,10,0,10));
		pg.setNumberGeometries(3);
		pg.setGeometryFactory(geometryFactory);
		
		MultiPoint pt = (MultiPoint) pg.create();
		checkRoundTrip(pt);
	}
}
