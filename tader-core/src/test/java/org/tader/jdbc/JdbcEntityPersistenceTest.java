package org.tader.jdbc;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.tader.ByteArrayInputStreamTypeCoercerContribution;
import org.tader.Entity;
import org.tader.EntityPersistence;
import org.tader.EntitySchema;
import org.tader.TestUtils;
import org.tader.TypeCoercer;
import org.tader.TypeCoercerContribution;
import org.tader.TypeCoercerImpl;
public class JdbcEntityPersistenceTest {

	@Test
	public void testPersistence() {
		for (DatabaseVendor vendor : DatabaseVendor.values()) {
			testPersistence(vendor);
		}
	}

	@Test
	public void testIdentity() {
		for (DatabaseVendor vendor : DatabaseVendor.values()) {
			testIdentity(vendor);
		}
	}

	@Test
	public void testBlob() {
		for (DatabaseVendor vendor : DatabaseVendor.values()) {
			testBlob(vendor);
		}
	}
	
	private void testPersistence(DatabaseVendor vendor) {
		ConnectionSource connectionSource = TestUtils.newConnectionSource(vendor);
		TestUtils.createTableAuthor(vendor, connectionSource);
		
		EntityPersistence persistence = createEntityPersistence(connectionSource);
		
		persistence.insert("author", TestUtils.createMap("authorId", 1, "authorName", "name1"));
		persistence.insert("author", TestUtils.createMap("authorId", 2, "authorName", "name2"));
		
		Entity author1 = persistence.get("author", 1);
		assertEquals("name1", author1.getString("authorName"));

		Entity author2 = persistence.get("author", 2);
		assertEquals("name2", author2.getString("authorName"));
	}

	private void testIdentity(DatabaseVendor vendor) {
		ConnectionSource connectionSource = TestUtils.newConnectionSource(vendor);
		TestUtils.createTableHasIdentity(vendor, connectionSource);
		
		EntityPersistence persistence = createEntityPersistence(connectionSource);
		
		Number pk1 = (Number) persistence.insert("hasIdentity", TestUtils.createMap("name", "name1"));
		Number pk2 = (Number) persistence.insert("hasIdentity", TestUtils.createMap("name", "name2"));

		assertEquals(1, pk1.intValue());
		assertEquals(2, pk2.intValue());
		
		assertEquals("name1", persistence.get("hasIdentity", 1).getString("name"));
		assertEquals("name2", persistence.get("hasIdentity", 2).getString("name"));
	}

	private void testBlob(DatabaseVendor vendor) {
		ConnectionSource connectionSource = TestUtils.newConnectionSource(vendor);
		TestUtils.createTableHasBlob(vendor, connectionSource);
		
		EntityPersistence persistence = createEntityPersistence(connectionSource);
		
		byte[] bytes = "HEllOwORlD!!".getBytes();
		Number pk = (Number) persistence.insert("hasBlob", TestUtils.createMap("id", 1, "binaryData", bytes));
		assertEquals(1, pk.intValue());
		
		byte[] persistedBytes = persistence.get("hasBlob", 1).getValue("binaryData", byte[].class);
		assertArrayEquals(bytes, persistedBytes);
	}
	
	private EntityPersistence createEntityPersistence(ConnectionSource connectionSource) {
		JdbcTemplate template = new JdbcTemplateImpl(connectionSource);
		TypeCoercer typeCoercer = new TypeCoercerImpl(createTypeCoercerContributions());
		NameTranslator nameTranslator = new UpperCamelNameTranslator();
		BlobTypeAnalyzer blobAnalyzer = new BlobTypeAnalyzerImpl();
		SelectHandlerSource selectHandlerSource = new SelectHandlerSourceImpl(blobAnalyzer);
		InsertHandlerSource insertHandlerSource = new InsertHandlerSourceImpl(typeCoercer, blobAnalyzer);
		EntitySchema schema = new JdbcEntitySchema(template, nameTranslator);
		EntityPersistence persistence = new JdbcEntityPersistence(schema, template, typeCoercer, nameTranslator, selectHandlerSource, insertHandlerSource);
		return persistence;
	}

	@SuppressWarnings("rawtypes")
	private Collection<TypeCoercerContribution> createTypeCoercerContributions() {
		List<TypeCoercerContribution> contributions = new ArrayList<TypeCoercerContribution>();
		contributions.add(new ByteArrayInputStreamTypeCoercerContribution());
		return contributions;
	}
}
