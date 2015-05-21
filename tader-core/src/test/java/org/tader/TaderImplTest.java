package org.tader;

import static org.junit.Assert.assertEquals;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.tader.AutoGenerateSource;
import org.tader.AutoGenerateSourceContribution;
import org.tader.AutoGenerateSourceImpl;
import org.tader.AutoGenerateStrategy;
import org.tader.DefaultStringAutoGenerateStrategy;
import org.tader.Entity;
import org.tader.EntityPersistence;
import org.tader.EntitySchema;
import org.tader.Tader;
import org.tader.TaderImpl;
import org.tader.IntegerLongTypeCoercerContribution;
import org.tader.PartialEntity;
import org.tader.PropertyDef;
import org.tader.TypeCoercer;
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
	
	private void testPartialDependency(TestJdbcTemplate template) {
		TestUtils.createTableAuthor(template);
		TestUtils.createTableBook(template);
		
		Tader grater = createGrater(template);
		
		PartialEntity partialAuthor = new PartialEntity("author").withValue("authorName", "foo");
		
		PartialEntity bookPartial = new PartialEntity("book").withValue("authorId", partialAuthor);
		
		Entity book = grater.insert(bookPartial);
		
		Entity author = book.getEntity("authorId");
		assertEquals(201, book.getInteger("bookId").intValue());
		
		assertEquals("foo", author.getString("authorName"));
		assertEquals(101, author.getInteger("authorId").intValue());
	}

	private void testImplicitDependency(TestJdbcTemplate template) {
		TestUtils.createTableAuthor(template);
		TestUtils.createTableBook(template);
		
		Tader grater = createGrater(template);
		
		PartialEntity bookPartial = new PartialEntity("book");
		
		Entity book = grater.insert(bookPartial);
		
		Entity author = book.getEntity("authorId");
		
		assertEquals("authorName1", author.getString("authorName"));
		assertEquals(101, author.getInteger("authorId").intValue());
		assertEquals(101L, author.getLong("authorId").longValue());
	}
	
	private void testExplicitDependency(TestJdbcTemplate template) {
		TestUtils.createTableAuthor(template);
		TestUtils.createTableBook(template);
		
		Tader grater = createGrater(template);
		
		Entity author1 = grater.insert(new PartialEntity("author"));
		assertEquals(101, author1.getInteger("authorId").intValue());

		Entity author2 = grater.insert(new PartialEntity("author"));
		assertEquals(102, author2.getInteger("authorId").intValue());
		
		PartialEntity bookPartial = new PartialEntity("book").withValue("authorId", author2);
		Entity book = grater.insert(bookPartial);
		assertEquals(201, book.getInteger("bookId").intValue());
		assertEquals(102, book.getEntity("authorId").getValue("authorId", Integer.class).intValue());
	}

	private Tader createGrater(TestJdbcTemplate template) {
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

	private Collection<TypeCoercerContribution<?, ?>> createTypeCoercerContributions() {
		List<TypeCoercerContribution<?, ?>> contributions = new ArrayList<TypeCoercerContribution<?, ?>>();
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
