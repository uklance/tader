package org.tader;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.tader.builder.TaderBuilder;
import org.tader.jdbc.ConnectionSource;
import org.tader.jdbc.DatabaseVendor;
import org.tader.jdbc.NameTranslator;
import org.tader.jdbc.UpperCamelNameTranslator;

public class TaderIntegrationTest {
	@Test
	public void testPartialDependency() {
		for (DatabaseVendor vendor : DatabaseVendor.values()) {
			testPartialDependency(vendor);
		}
	}

	@Test
	public void testImplicitDependency() {
		for (DatabaseVendor vendor : DatabaseVendor.values()) {
			testImplicitDependency(vendor);
		}
	}

	@Test
	public void testExplicitDependency() {
		for (DatabaseVendor vendor : DatabaseVendor.values()) {
			testExplicitDependency(vendor);
		}
	}
	
	@Test
	public void testDelete() {
		for (DatabaseVendor vendor : DatabaseVendor.values()) {
			testDelete(vendor);
		}
	}
	
	private void testPartialDependency(DatabaseVendor vendor) {
		ConnectionSource connectionSource = TestUtils.newConnectionSource(vendor);
		TestUtils.createTableAuthor(vendor, connectionSource);
		TestUtils.createTableBook(vendor, connectionSource);
		
		Tader tader = createTader(connectionSource);
		
		PartialEntity authorPartial = new PartialEntity("author").withValue("authorName", "foo");
		
		PartialEntity bookPartial = new PartialEntity("book").withValue("authorId", authorPartial);
		
		Entity book = tader.insert(bookPartial);
		
		Entity author = book.getEntity("authorId");
		assertEquals(200, book.getInteger("bookId").intValue());
		
		assertEquals("foo", author.getString("authorName"));
		assertEquals(100, author.getInteger("authorId").intValue());
	}

	private void testImplicitDependency(DatabaseVendor vendor) {
		ConnectionSource connectionSource = TestUtils.newConnectionSource(vendor);
		TestUtils.createTableAuthor(vendor, connectionSource);
		TestUtils.createTableBook(vendor, connectionSource);
		
		Tader tader = createTader(connectionSource);
		
		PartialEntity bookPartial = new PartialEntity("book");
		
		Entity book = tader.insert(bookPartial);
		
		Entity author = book.getEntity("authorId");
		
		assertEquals("authorName0", author.getString("authorName"));
		assertEquals(100, author.getInteger("authorId").intValue());
		assertEquals(100L, author.getLong("authorId").longValue());
	}
	
	private void testExplicitDependency(DatabaseVendor vendor) {
		ConnectionSource connectionSource = TestUtils.newConnectionSource(vendor);
		TestUtils.createTableAuthor(vendor, connectionSource);
		TestUtils.createTableBook(vendor, connectionSource);
		
		Tader tader = createTader(connectionSource);
		
		Entity author1 = tader.insert(new PartialEntity("author"));
		assertEquals(100, author1.getInteger("authorId").intValue());

		Entity author2 = tader.insert(new PartialEntity("author"));
		assertEquals(101, author2.getInteger("authorId").intValue());
		
		PartialEntity bookPartial = new PartialEntity("book").withValue("authorId", author2);
		Entity book = tader.insert(bookPartial);
		assertEquals(200, book.getInteger("bookId").intValue());
		assertEquals(101, book.getEntity("authorId").getValue("authorId", Integer.class).intValue());
	}

	private void testDelete(DatabaseVendor vendor) {
		ConnectionSource connectionSource = TestUtils.newConnectionSource(vendor);
		TestUtils.createTableAuthor(vendor, connectionSource);
		TestUtils.createTableBook(vendor, connectionSource);
		
		Tader tader = createTader(connectionSource);
		
		PartialEntity authorPartial = new PartialEntity("author");
		
		List<Entity> inserted = tader.insert(authorPartial, 3);
		
		assertEquals(3, TestUtils.getAuthorCount(connectionSource));
		
		tader.delete(inserted.get(0));
		assertEquals(2, TestUtils.getAuthorCount(connectionSource));

		tader.delete(inserted.get(1));
		assertEquals(1, TestUtils.getAuthorCount(connectionSource));

		tader.delete(inserted.get(2));
		assertEquals(0, TestUtils.getAuthorCount(connectionSource));
	}

	private Tader createTader(ConnectionSource connectionSource) {
		TaderBuilder builder = new TaderBuilder()
			.withCoreServices()
			.withCoreJdbcServices()
			.withCoreTypeCoercerContributions()
			.withCoreAutoGenerateSourceContributions()
			.withContribution(AutoGenerateSource.class, createAutoGenerateSourceContribution())
			.withServiceInstance(NameTranslator.class, UpperCamelNameTranslator.class)
			.withConnectionSource(connectionSource);
		
		return builder.build();
	}

	private AutoGenerateSourceContribution createAutoGenerateSourceContribution() {
		
		AutoGenerateStrategy countFromOneHundred = new AutoGenerateStrategy() {
			@Override
			public Object generate(PropertyDef propDef, int increment) {
				return 100 + increment;
			}
		};
		AutoGenerateStrategy countFromTwoHundred = new AutoGenerateStrategy() {
			@Override
			public Object generate(PropertyDef propDef, int increment) {
				return 200 + increment;
			}
		};
		
		AutoGenerateSourceContribution contribution = new AutoGenerateSourceContribution()
			.withAutoGenerateStrategy("author", "authorId", countFromOneHundred)
			.withAutoGenerateStrategy("book",  "bookId", countFromTwoHundred);
		
		return contribution;
	}
}
