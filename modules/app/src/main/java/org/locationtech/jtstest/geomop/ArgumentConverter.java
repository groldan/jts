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

package org.locationtech.jtstest.geomop;

public class ArgumentConverter 
{
	public ArgumentConverter()
	{
		
	}
	
  public Object[] convert(Class[] parameterTypes, Object[] args)
  {
  	Object[] actualArgs = new Object[args.length];
    for (int i = 0; i < args.length; i++ ) {
    	actualArgs[i] = convert(parameterTypes[i], args[i]);
    }
    return actualArgs;
  }

  public Object convert(Class destClass, Object srcValue)
  {
    if (srcValue instanceof String) {
      return convertFromString(destClass, (String) srcValue);
    }
    if (destClass.isAssignableFrom(srcValue.getClass())) {
      return srcValue;
    }
    throwInvalidConversion(destClass, srcValue);
    return null;
  }

  private Object convertFromString(Class destClass, String src)
  {
    if (destClass == Boolean.class || destClass == boolean.class) {
      if (src.equals("true")) {
        return new Boolean(true);
      }
      else if (src.equals("false")) {
        return new Boolean(false);
      }
      throwInvalidConversion(destClass, src);
    }
    else if (destClass == Integer.class || destClass == int.class) {
      // try as an int
      try {
        return new Integer(src);
      }
      catch (NumberFormatException e) {
        // eat this exception - it will be reported below
      }
    }
    else if (destClass == Double.class || destClass == double.class) {
      // try as an int
      try {
        return new Double(src);
      }
      catch (NumberFormatException e) {
        // eat this exception - it will be reported below
      }
    }
    else if (destClass == String.class) {
      return src;
    }
    throwInvalidConversion(destClass, src);
    return null;
  }

  private void throwInvalidConversion(Class destClass, Object srcValue)
  {
  	throw new IllegalArgumentException("Cannot convert " + srcValue + " to " + destClass.getName());
  }
}
