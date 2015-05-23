package org.tader;

import static org.junit.Assert.assertEquals;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.tader.jdbc.BlobTypeAnalyzer;
import org.tader.jdbc.BlobTypeAnalyzerImpl;
import org.tader.jdbc.InsertHandlerSource;
import org.tader.jdbc.InsertHandlerSourceImpl;
import org.tader.jdbc.JdbcEntityPersistence;
import org.tader.jdbc.JdbcEntitySchema;
import org.tader.jdbc.NameTranslator;
import org.tader.jdbc.SelectHandlerSource;
import org.tader.jdbc.SelectHandlerSourceImpl;
import org.tader.jdbc.TestJdbcTemplate;
import org.tader.jdbc.TypeCoercerContribution;
import org.tader.jdbc.TypeCoercerImpl;
import org.tader.jdbc.UpperCamelNameTranslator;

public class TaderImplTest {

	@Test
	public void testPartialDependency() {
		for (TestJdbcTemplate template : TestUtils.getTestJdbcTemplates()) {
			testPartialDependency(template);
		}
	}

	@Test
	public void testImplicitDependency() {
		for (TestJdbcTemplate template : TestUtils.getTestJdbcTemplates()) {
			testImplicitDependency(template);
		}
	}

	@Test
	public void testExplicitDependency() {
		for (TestJdbcTemplate template : TestUtils.getTestJdbcTemplates()) {
			testExplicitDependency(template);
		}
	}
	
	@Test
	public void testDelete() {
		for (TestJdbcTemplate template : TestUtils.getTestJdbcTemplates()) {
			testDelete(template);
		}
	}
	
	private void testDelete(TestJdbcTemplate template) {
		TestUtils.createTableAuthor(template);
		TestUtils.createTableBook(template);
		
		Tader tader = createTader(template);
		
		PartialEntity authorPartial = new PartialEntity("author");
		
		List<Entity> inserted = tader.insert(authorPartial, 3);
		
		assertEquals(3, TestUtils.getAuthorCount(template));
		
		tader.delete(inserted.get(0));
		assertEquals(2, TestUtils.getAuthorCount(template));

		tader.delete(inserted.get(1));
		assertEquals(1, TestUtils.getAuthorCount(template));

		tader.delete(inserted.get(2));
		assertEquals(0, TestUtils.getAuthorCount(template));
	}

	private void testPartialDependency(TestJdbcTemplate template) {
		TestUtils.createTableAuthor(template);
		TestUtils.createTableBook(template);
		
		Tader tader = createTader(template);
		
		PartialEntity authorPartial = new PartialEntity("author").withValue("authorName", "foo");
		
		PartialEntity bookPartial = new PartialEntity("book").withValue("authorId", authorPartial);
		
		Entity book = tader.insert(bookPartial);
		
		Entity author = book.getEntity("authorId");
		assertEquals(200, book.getInteger("bookId").intValue());
		
		assertEquals("foo", author.getString("authorName"));
		assertEquals(100, author.getInteger("authorId").intValue());
	}

	private void testImplicitDependency(TestJdbcTemplate template) {
		TestUtils.createTableAuthor(template);
		TestUtils.createTableBook(template);
		
		Tader tader = createTader(template);
		
		PartialEntity bookPartial = new PartialEntity("book");
		
		Entity book = tader.insert(bookPartial);
		
		Entity author = book.getEntity("authorId");
		
		assertEquals("authorName0", author.getString("authorName"));
		assertEquals(100, author.getInteger("authorId").intValue());
		assertEquals(100L, author.getLong("authorId").longValue());
	}
	
	private void testExplicitDependency(TestJdbcTemplate template) {
		TestUtils.createTableAuthor(template);
		TestUtils.createTableBook(template);
		
		Tader tader = createTader(template);
		
		Entity author1 = tader.insert(new PartialEntity("author"));
		assertEquals(100, author1.getInteger("authorId").intValue());

		Entity author2 = tader.insert(new PartialEntity("author"));
		assertEquals(101, author2.getInteger("authorId").intValue());
		
		PartialEntity bookPartial = new PartialEntity("book").withValue("authorId", author2);
		Entity book = tader.insert(bookPartial);
		assertEquals(200, book.getInteger("bookId").intValue());
		assertEquals(101, book.getEntity("authorId").getValue("authorId", Integer.class).intValue());
	}

	private Tader createTader(TestJdbcTemplate template) {
		NameTranslator nameTranslator = new UpperCamelNameTranslator();
		EntitySchema schema = new JdbcEntitySchema(template, nameTranslator);
		TypeCoercer typeCoercer = new TypeCoercerImpl(createTypeCoercerContributions());
		BlobTypeAnalyzer blobAnalyzer = new BlobTypeAnalyzerImpl();
		SelectHandlerSource selectHandlerSource = new SelectHandlerSourceImpl(blobAnalyzer);
		InsertHandlerSource insertHandlerSource = new InsertHandlerSourceImpl(typeCoercer, blobAnalyzer);
		EntityPersistence persistence = new JdbcEntityPersistence(schema, template, typeCoercer, nameTranslator, selectHandlerSource,
				insertHandlerSource);
		AutoGenerateSource autoGenerateSource = new AutoGenerateSourceImpl(schema, createAutoGenerateSourceContributions());
		return new TaderImpl(schema, persistence, autoGenerateSource);
	}

	@SuppressWarnings("rawtypes")
	private Collection<TypeCoercerContribution> createTypeCoercerContributions() {
		List<TypeCoercerContribution> contributions = new ArrayList<TypeCoercerContribution>();
		contributions.add(new IntegerLongTypeCoercerContribution());
		return contributions;
	}

	private Collection<AutoGenerateSourceContribution> createAutoGenerateSourceContributions() {
		
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
			.withAutoGenerateStrategy("book",  "bookId", countFromTwoHundred)
			.withAutoGenerateStrategy(Types.VARCHAR, new DefaultStringAutoGenerateStrategy());
		
		return Collections.singleton(contribution);
	}
}
