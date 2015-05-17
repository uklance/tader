package org.grater.jdbc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.grater.Entity;
import org.grater.EntityPersistence;
import org.grater.EntitySchema;
import org.grater.TestUtils;
import org.grater.TypeCoercer;
import org.junit.Test;
public class JdbcEntityPersistenceTest {

	@Test
	public void testPersistence() {
		for (TestJdbcTemplate template : TestUtils.getTestJdbcTemplates()) {
			testPersistence(template);
		}
	}

	@Test
	public void testIdentity() {
		for (TestJdbcTemplate template : TestUtils.getTestJdbcTemplates()) {
			testIdentity(template);
		}
	}

	@Test
	public void testBlob() {
		for (TestJdbcTemplate template : TestUtils.getTestJdbcTemplates()) {
			testBlob(template);
		}
	}
	
	private void testPersistence(TestJdbcTemplate template) {
		TestUtils.createTableAuthor(template);
		
		EntityPersistence persistence = createEntityPersistence(template);
		
		persistence.insert("author", TestUtils.createMap("authorId", 1, "authorName", "name1"));
		persistence.insert("author", TestUtils.createMap("authorId", 2, "authorName", "name2"));
		
		Entity author1 = persistence.get("author", 1);
		assertEquals("name1", author1.getString("authorName"));

		Entity author2 = persistence.get("author", 2);
		assertEquals("name2", author2.getString("authorName"));
	}

	private void testIdentity(TestJdbcTemplate template) {
		TestUtils.createTableHasIdentity(template);
		
		EntityPersistence persistence = createEntityPersistence(template);
		
		Number pk1 = (Number) persistence.insert("hasIdentity", TestUtils.createMap("name", "name1"));
		Number pk2 = (Number) persistence.insert("hasIdentity", TestUtils.createMap("name", "name2"));

		assertEquals(1, pk1.intValue());
		assertEquals(2, pk2.intValue());
		
		assertEquals("name1", persistence.get("hasIdentity", 1).getString("name"));
		assertEquals("name2", persistence.get("hasIdentity", 2).getString("name"));
	}

	private void testBlob(TestJdbcTemplate template) {
		TestUtils.createTableHasBlob(template);
		
		EntityPersistence persistence = createEntityPersistence(template);
		
		byte[] bytes = "HEllOwORlD!!".getBytes();
		Number pk = (Number) persistence.insert("hasBlob", TestUtils.createMap("id", 1, "binaryData", bytes));
		assertEquals(1, pk.intValue());
		
		byte[] persistedBytes = persistence.get("hasBlob", 1).getValue("binaryData", byte[].class);
		assertArrayEquals(bytes, persistedBytes);
	}
	
	private EntityPersistence createEntityPersistence(TestJdbcTemplate template) {
		TypeCoercer typeCoercer = new TypeCoercerImpl(createTypeCoercerContributions());
		NameTranslator nameTranslator = new UpperCamelNameTranslator();
		BlobTypeAnalyzer blobAnalyzer = new BlobTypeAnalyzerImpl();
		SelectHandlerSource selectHandlerSource = new SelectHandlerSourceImpl(blobAnalyzer);
		InsertHandlerSource insertHandlerSource = new InsertHandlerSourceImpl(typeCoercer, blobAnalyzer);
		EntitySchema schema = new JdbcEntitySchema(template, nameTranslator);
		EntityPersistence persistence = new JdbcEntityPersistence(schema, template, typeCoercer, nameTranslator, selectHandlerSource, insertHandlerSource);
		return persistence;
	}

	private Collection<TypeCoercerContribution<?, ?>> createTypeCoercerContributions() {
		List<TypeCoercerContribution<?, ?>> contributions = new ArrayList<TypeCoercerContribution<?,?>>();
		contributions.add(new ByteArrayInputStreamTypeCoercerContribution());
		return contributions;
	}
}
