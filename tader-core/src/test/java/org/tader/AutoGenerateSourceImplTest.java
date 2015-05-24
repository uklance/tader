package org.tader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class AutoGenerateSourceImplTest {
	@Test
	public void testGetAutoGeneratePropertyNames() {
		EntitySchema schema = mock(EntitySchema.class);
		
		AutoGenerateSource autoGenSource = new AutoGenerateSourceImpl(schema, Collections.<AutoGenerateSourceContribution>emptyList());
		
		PropertyDef propDef1 = new PropertyDef("someEntity", "someTable", "prop1", "column1");
		PropertyDef propDef2 = new PropertyDef("someEntity", "someTable", "prop2", "column2");
		PropertyDef propDef3 = new PropertyDef("someEntity", "someTable", "prop3", "column3");

		when(schema.getPropertyDefs("someEntity")).thenReturn(Arrays.asList(propDef1, propDef2, propDef3));

		assertSet(autoGenSource.getAutoGeneratePropertyNames("someEntity"), "prop1", "prop2", "prop3");
		
		propDef1.setNullable(true);
		assertSet(autoGenSource.getAutoGeneratePropertyNames("someEntity"), "prop2", "prop3");

		propDef2.setAutoIncrement(true);
		assertSet(autoGenSource.getAutoGeneratePropertyNames("someEntity"), "prop3");

		propDef3.setGenerated(true);
		assertTrue(autoGenSource.getAutoGeneratePropertyNames("someEntity").isEmpty());
	}

	private void assertSet(Set<String> actual, String... expectedValues) {
		assertEquals(new HashSet<String>(Arrays.asList(expectedValues)), actual);
	}
}
