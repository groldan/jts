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
package org.locationtech.jts.io.oracle;

import java.sql.SQLException;

import org.locationtech.jts.generator.*;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.oracle.OraReader;
import org.locationtech.jts.io.oracle.OraWriter;

import oracle.sql.STRUCT;


/**
 * 
 * Does round trip testing by creating the oracle object, then decoding it. 
 * 
 * These tests do not include insert / delete / select operations.
 * 
 * NOTE: This test does require a precision to be used during the comparison, 
 * as points are rounded somewhat when creating the oracle struct. 
 * (One less decimal than a java double).
 *
 * @author David Zwiers, Vivid Solutions. 
 */
public class StaticMultiLineStringTest extends ConnectedTestCase {

	/**
	 * @param arg
	 */
	public StaticMultiLineStringTest(String arg) {
		super(arg);
	}

	/**
	 * Round Trip test for a single line string
	 * @throws SQLException 
	 */
	public void testSingleMultiLineStringRoundTrip() throws SQLException{
		LineStringGenerator pgc = new LineStringGenerator();
		pgc.setGeometryFactory(geometryFactory);
		pgc.setNumberPoints(10);
		MultiGenerator pg = new MultiGenerator(pgc);
		pg.setBoundingBox(new Envelope(0,10,0,10));
		pg.setNumberGeometries(3);
		pg.setGeometryFactory(geometryFactory);
		
		MultiLineString pt = (MultiLineString) pg.create();
		
		OraWriter ow = new OraWriter();
		STRUCT st = ow.write(pt, getConnection());
		
		OraReader or = new OraReader();
		MultiLineString pt2 = (MultiLineString) or.read(st);
		
//		System.out.println((pt==null?"NULL":pt.toString()));
//		System.out.println((pt2==null?"NULL":pt2.toString()));
		assertTrue("The input MultiLineString is not the same as the output MultiLineString",pt.equals(pt2));
	}

	/**
	 * Round Trip test for a 100 non overlapping line strings
	 * @throws SQLException 
	 */
	public void testGridMultiLineStringsRoundTrip() throws SQLException{
		GridGenerator grid = new GridGenerator();
		grid.setGeometryFactory(geometryFactory);
		grid.setBoundingBox(new Envelope(0,10,0,10));
		grid.setNumberColumns(10);
		grid.setNumberRows(10);
		
		MultiLineString[] pt = new MultiLineString[100];
		STRUCT[] st = new STRUCT[100];

		LineStringGenerator pgc = new LineStringGenerator();
		pgc.setGeometryFactory(geometryFactory);
		pgc.setNumberPoints(10);
		MultiGenerator pg = new MultiGenerator(pgc);
		pg.setBoundingBox(new Envelope(0,10,0,10));
		pg.setNumberGeometries(3);
		pg.setGeometryFactory(geometryFactory);
		
		OraWriter ow = new OraWriter();
		
		int i=0;
		while(grid.canCreate() && i<100){
			pg.setBoundingBox(grid.createEnv());
			pt[i] = (MultiLineString) pg.create();
			st[i] = ow.write(pt[i], getConnection());
			i++;
		}
		
		OraReader or = new OraReader();
		i=0;
		while(i<100 && pt[i] != null){
			MultiLineString pt2 = (MultiLineString) or.read(st[i]);
//			System.out.println((pt[i]==null?"NULL":pt[i].toString()));
//			System.out.println((pt2==null?"NULL":pt2.toString()));
			assertTrue("The input MultiLineString is not the same as the output MultiLineString",pt[i].equals(pt2));
			i++;
		}
	}

	/**
	 * Round Trip test for a 8 overlapping line strings (4 distinct line strings)
	 * @throws SQLException 
	 */
	public void testOverlappingMultiLineStringsRoundTrip() throws SQLException{
		GridGenerator grid = new GridGenerator();
		grid.setGeometryFactory(geometryFactory);
		grid.setBoundingBox(new Envelope(0,10,0,10));
		grid.setNumberColumns(2);
		grid.setNumberRows(2);
		
		MultiLineString[] pt = new MultiLineString[4];
		STRUCT[] st = new STRUCT[8];

		LineStringGenerator pgc = new LineStringGenerator();
		pgc.setGeometryFactory(geometryFactory);
		pgc.setNumberPoints(10);
		MultiGenerator pg = new MultiGenerator(pgc);
		pg.setBoundingBox(new Envelope(0,10,0,10));
		pg.setNumberGeometries(3);
		pg.setGeometryFactory(geometryFactory);
		
		OraWriter ow = new OraWriter();
		
		int i=0;
		while(grid.canCreate() && i<8){
			pg.setBoundingBox(grid.createEnv());
			pt[i] = (MultiLineString) pg.create();
			st[i] = ow.write(pt[i], getConnection());
			i++;
		}
		for(int j=0;j<4;j++){
			if(pt[j]!=null)
				st[i++] = ow.write(pt[j], getConnection());
		}
		
		OraReader or = new OraReader();
		i=0;
		while(i<8 && pt[i%4] != null){
			MultiLineString pt2 = (MultiLineString) or.read(st[i]);
//			System.out.println((pt==null?"NULL":pt[i%4].toString()));
//			System.out.println((pt2==null?"NULL":pt2.toString()));
			assertTrue("The input MultiLineString is not the same as the output MultiLineString",pt[i%4].equals(pt2));
			i++;
		}
	}

	/**
	 * Round Trip test for a single line string with lotsa points
	 * @throws SQLException 
	 */
	public void testSingleMultiLineStringManyPointRoundTrip() throws SQLException{

		LineStringGenerator pgc = new LineStringGenerator();
		pgc.setGeometryFactory(geometryFactory);
		pgc.setNumberPoints(1000);
		pgc.setGenerationAlgorithm(LineStringGenerator.HORZ);
		MultiGenerator pg = new MultiGenerator(pgc);
		pg.setBoundingBox(new Envelope(0,10,0,10));
		pg.setNumberGeometries(3);
		pg.setGeometryFactory(geometryFactory);
		
		MultiLineString pt = (MultiLineString) pg.create();
//		System.out.println((pt==null?"NULL":pt.toString()));
		
		OraWriter ow = new OraWriter();
		STRUCT st = ow.write(pt, getConnection());
		
		OraReader or = new OraReader();
		MultiLineString pt2 = (MultiLineString) or.read(st);

//		System.out.println((pt==null?"NULL":pt.toString()));
//		System.out.println((pt2==null?"NULL":pt2.toString()));
		assertTrue("The input MultiLineString is not the same as the output MultiLineString",pt.equals(pt2));
	}
}
