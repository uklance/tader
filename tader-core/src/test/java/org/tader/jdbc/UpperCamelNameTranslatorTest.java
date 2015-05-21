package org.tader.jdbc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.tader.jdbc.NameTranslator;
import org.tader.jdbc.UpperCamelNameTranslator;

public class UpperCamelNameTranslatorTest {

	@Test
	public void testTranslate() {
		NameTranslator nameTranslator = new UpperCamelNameTranslator();
		
		assertEquals("oneTwoThree", nameTranslator.getPropertyForColumn("foo", "ONE_TWO_THREE"));
		assertEquals("ONE_TWO_THREE", nameTranslator.getTableForEntity("oneTwoThree"));
	}

}
