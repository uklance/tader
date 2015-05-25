package org.tader;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
	
	@Test
	public void testAllColumnTypesAutogenerate() {
		for (DatabaseVendor vendor : DatabaseVendor.values()) {
			testAllColumnTypesAutogenerate(vendor);
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

	private void testAllColumnTypesAutogenerate(DatabaseVendor vendor) {
		ConnectionSource connectionSource = TestUtils.newConnectionSource(vendor);
		Tader tader = new TaderBuilder()
			.withCoreServices()
			.withCoreJdbcServices()
			.withCoreTypeCoercerContributions()
			.withCoreAutoGenerateSourceContributions()
			.withServiceInstance(NameTranslator.class, UpperCamelNameTranslator.class)
			.withConnectionSource(connectionSource)
			.build();
	
		TestUtils.createTableHasAllTypes(vendor, connectionSource);
		PartialEntity template = new PartialEntity("hasAllTypes");
		
		List<Entity> entities = tader.insert(template, 3);

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		List<Date> expectedDates = new ArrayList<Date>();
		expectedDates.add(cal.getTime());
		cal.add(Calendar.DATE, 1);
		expectedDates.add(cal.getTime());
		cal.add(Calendar.DATE, 1);
		expectedDates.add(cal.getTime());
		
		assertColumn(entities, "id", Integer.class, 0, 1, 2);
		assertColumn(entities, "intRequired", Integer.class, 0, 1, 2);
		assertNullColumn(entities, "intNullable");
		assertColumn(entities, "varcharRequired", String.class, "varcharRequired0", "varcharRequired1", "varcharRequired2");
		assertNullColumn(entities, "varcharNullable");
		assertByteArrayColumn(entities, "blobRequired", "blobRequired0".getBytes(), "blobRequired1".getBytes(), "blobRequired2".getBytes());
		assertNullColumn(entities, "blobNullable");
		assertColumn(entities, "dateRequired", Date.class, expectedDates.toArray());
		assertNullColumn(entities, "dateNullable");
		assertColumn(entities, "timestampRequired", Date.class, expectedDates.toArray());
		assertNullColumn(entities, "timestampNullable");
		assertColumn(entities, "decimalRequired", BigDecimal.class, new BigDecimal("0.00"), new BigDecimal("1.00"), new BigDecimal("2.00"));
		assertNullColumn(entities, "decimalNullable");
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

	private void assertNullColumn(List<Entity> entities, String propertyName) {
		for (Entity entity : entities) {
			assertNull(entity.getValue(propertyName));
		}
	}

	private void assertColumn(List<Entity> entities, String propertyName, Class<?> type, Object... expected) {
		List<Object> actual = new ArrayList<Object>();
		for (Entity entity : entities) {
			actual.add(entity.getValue(propertyName, type));
		}
		assertEquals(Arrays.asList(expected), actual);
	}
	
	private void assertByteArrayColumn(List<Entity> entities, String propertyName, byte[]... expected) {
		assertEquals(entities.size(), expected.length);
		for (int i = 0; i < entities.size(); ++i) {
			Entity entity = entities.get(i);
			byte[] actual = entity.getValue(propertyName, byte[].class);
			assertArrayEquals(expected[i], actual);
		}
	}
}
