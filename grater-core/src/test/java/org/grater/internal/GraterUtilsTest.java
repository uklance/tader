package org.grater.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GraterUtilsTest extends GraterUtils {

	@Test
	public void toCamelCaseTest() {
		assertEquals("oneTwoThree", GraterUtils.toCamelCase("one_two_three"));
		assertEquals("oneTwoThree", GraterUtils.toCamelCase("ONE_TWO_THREE"));
		assertEquals("oneTwo", GraterUtils.toCamelCase("one_two_"));
		assertEquals("onetwo", GraterUtils.toCamelCase("oneTwo"));
	}
}
