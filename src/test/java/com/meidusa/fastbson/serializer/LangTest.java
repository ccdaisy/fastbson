/*   
 *   Created   on   2005-1-29   
 */
package com.meidusa.fastbson.serializer;

import junit.framework.TestCase;

/**
 * <title> LangTest <description> <company>
 * 
 * @version
 * @since
 * 
 *        2005-1-29 16:19:28
 */
public class LangTest extends TestCase {
	/**
	 * Constructor for LangTest.
	 * 
	 * @param name
	 */
	public LangTest(String name) {
		super(name);
	}
	
//	public void testIsAssignedFrom1() {
//		assertTrue(String.class.isAssignableFrom(Object.class));
//	}

	public void testIsAssignedFrom2() {
		assertTrue(Object.class.isAssignableFrom(Object.class));
	}

	public void testIsAssignedFrom3() {
		assertTrue(Object.class.isAssignableFrom(String.class));
	}

	public void testInstanceOf1() {
		String ss = "";
		assertTrue(ss instanceof Object);
	}

	public void testInstanceOf2() {
		Object o = new Object();
		assertTrue(o instanceof Object);
	}
}