package org.tader.builder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.tader.AutoGenerateSource;
import org.tader.AutoGenerateSourceContribution;
import org.tader.AutoGenerateStrategy;
import org.tader.Entity;
import org.tader.PartialEntity;
import org.tader.PropertyDef;
import org.tader.Tader;
import org.tader.TestUtils;
import org.tader.jdbc.ConnectionSource;
import org.tader.jdbc.DatabaseVendor;
import org.tader.jdbc.NameTranslator;
import org.tader.jdbc.UpperCamelNameTranslator;

public class TaderBuilderTest {

	@Test
	public void testPartialDependency() {
		for (DatabaseVendor vendor : DatabaseVendor.values()) {
			ConnectionSource connectionSource = TestUtils.newConnectionSource(vendor);
			testPartialDependency(connectionSource);
		}
	}

	@Test
	public void testImplicitDependency() {
		for (DatabaseVendor vendor : DatabaseVendor.values()) {
			ConnectionSource connectionSource = TestUtils.newConnectionSource(vendor);
			testImplicitDependency(connectionSource);
		}
	}

	@Test
	public void testExplicitDependency() {
		for (DatabaseVendor vendor : DatabaseVendor.values()) {
			ConnectionSource connectionSource = TestUtils.newConnectionSource(vendor);
			testExplicitDependency(connectionSource);
		}
	}
	
	private void testPartialDependency(ConnectionSource connectionSource) {
		TestUtils.createTableAuthor(connectionSource);
		TestUtils.createTableBook(connectionSource);
		
		Tader tader = createTader(connectionSource);
		
		PartialEntity partialAuthor = new PartialEntity("author").withValue("authorName", "foo");
		
		PartialEntity bookPartial = new PartialEntity("book").withValue("authorId", partialAuthor);
		
		Entity book = tader.insert(bookPartial);
		
		Entity author = book.getEntity("authorId");
		assertEquals(201, book.getInteger("bookId").intValue());
		
		assertEquals("foo", author.getString("authorName"));
		assertEquals(101, author.getInteger("authorId").intValue());
	}

	private void testImplicitDependency(ConnectionSource connectionSource) {
		TestUtils.createTableAuthor(connectionSource);
		TestUtils.createTableBook(connectionSource);
		
		Tader tader = createTader(connectionSource);
		
		PartialEntity bookPartial = new PartialEntity("book");
		
		Entity book = tader.insert(bookPartial);
		
		Entity author = book.getEntity("authorId");
		
		assertEquals("authorName1", author.getString("authorName"));
		assertEquals(101, author.getInteger("authorId").intValue());
		assertEquals(101L, author.getLong("authorId").longValue());
	}
	
	private void testExplicitDependency(ConnectionSource connectionSource) {
		TestUtils.createTableAuthor(connectionSource);
		TestUtils.createTableBook(connectionSource);
		
		Tader tader = createTader(connectionSource);
		
		Entity author1 = tader.insert(new PartialEntity("author"));
		assertEquals(101, author1.getInteger("authorId").intValue());

		Entity author2 = tader.insert(new PartialEntity("author"));
		assertEquals(102, author2.getInteger("authorId").intValue());
		
		PartialEntity bookPartial = new PartialEntity("book").withValue("authorId", author2);
		Entity book = tader.insert(bookPartial);
		assertEquals(201, book.getInteger("bookId").intValue());
		assertEquals(102, book.getEntity("authorId").getValue("authorId", Integer.class).intValue());
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
