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
		
		AutoGenerateSource autoGenSource1 = new AutoGenerateSourceImpl(schema, Collections.<AutoGenerateSourceContribution>emptyList(), false);
		AutoGenerateSource autoGenSource2 = new AutoGenerateSourceImpl(schema, Collections.<AutoGenerateSourceContribution>emptyList(), true);
		
		MutablePropertyDef propDef1 = new MutablePropertyDef("someEntity", "someTable", "prop1", "column1");
		MutablePropertyDef propDef2 = new MutablePropertyDef("someEntity", "someTable", "prop2", "column2");
		MutablePropertyDef propDef3 = new MutablePropertyDef("someEntity", "someTable", "prop3", "column3");

		when(schema.getPropertyDefs("someEntity")).thenReturn(Arrays.<PropertyDef> asList(propDef1, propDef2, propDef3));

		assertSet(autoGenSource1.getAutoGeneratePropertyNames("someEntity"), "prop1", "prop2", "prop3");
		assertSet(autoGenSource2.getAutoGeneratePropertyNames("someEntity"), "prop1", "prop2", "prop3");
		
		propDef1.setNullable(true);
		assertSet(autoGenSource1.getAutoGeneratePropertyNames("someEntity"), "prop2", "prop3");
		assertSet(autoGenSource2.getAutoGeneratePropertyNames("someEntity"), "prop1", "prop2", "prop3");

		propDef2.setAutoIncrement(true);
		assertSet(autoGenSource1.getAutoGeneratePropertyNames("someEntity"), "prop3");
		assertSet(autoGenSource2.getAutoGeneratePropertyNames("someEntity"), "prop1", "prop3");

		propDef3.setGenerated(true);
		assertTrue(autoGenSource1.getAutoGeneratePropertyNames("someEntity").isEmpty());
		assertSet(autoGenSource2.getAutoGeneratePropertyNames("someEntity"), "prop1");
	}

	private void assertSet(Set<String> actual, String... expectedValues) {
		assertEquals(new HashSet<String>(Arrays.asList(expectedValues)), actual);
	}
}
