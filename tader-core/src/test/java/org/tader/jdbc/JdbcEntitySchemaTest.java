package org.tader.jdbc;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.tader.EntitySchema;
import org.tader.PropertyDef;
import org.tader.TestUtils;

public class JdbcEntitySchemaTest {
	private static final Set<String> AUTHOR_COLUMN_NAMES = new HashSet<String>(Arrays.asList("AUTHOR_ID", "AUTHOR_NAME", "AUTHOR_HOBBY"));

	@Test
	public void testColumns() {
		for (DatabaseVendor vendor : DatabaseVendor.values()) {
			testColumns(vendor);
		}
	}

	private void testColumns(DatabaseVendor vendor) {
		ConnectionSource connectionSource = TestUtils.newConnectionSource(vendor);
		TestUtils.createTableAuthor(vendor, connectionSource);

		EntitySchema schema = createEntitySchema(connectionSource);

		assertEquals("authorId", schema.getPrimaryKeyPropertyName("author"));
		assertColumnNames(AUTHOR_COLUMN_NAMES, schema.getPropertyDefs("author"));
	}

	private EntitySchema createEntitySchema(ConnectionSource connectionSource) {
		JdbcTemplate template = new JdbcTemplateImpl(connectionSource);
		NameTranslator nameTranslator = new UpperCamelNameTranslator();
		return new JdbcEntitySchema(template, nameTranslator);
	}

	private void assertColumnNames(Set<String> expected, Collection<PropertyDef> propertyDefs) {
		Set<String> actual = new HashSet<String>();
		for (PropertyDef propDef : propertyDefs) {
			actual.add(propDef.getColumnName());
		}
		assertEquals(expected, actual);
	}
}
