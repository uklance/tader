package org.tader.builder;
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
import org.tader.Entity;
import org.tader.PartialEntity;
import org.tader.Tader;
import org.tader.TestUtils;
import org.tader.TypeCoercer;
import org.tader.jdbc.ConnectionSource;
import org.tader.jdbc.DatabaseVendor;
import org.tader.jdbc.NameTranslator;
import org.tader.jdbc.UpperCamelNameTranslator;

public class TaderBuilderTest {
	@Test
	public void testWithCoreTypeCoercerContributions() {
		Registry registry = new TaderBuilder()
			.withCoreServices()
			.withCoreTypeCoercerContributions()
			.buildRegistry();
		
		TypeCoercer typeCoercer = registry.getService(TypeCoercer.class);
		
		assertEquals(Integer.valueOf(1), typeCoercer.coerce(1L, Integer.class));
		assertEquals(Long.valueOf(1), typeCoercer.coerce(1, Long.class));
		assertEquals(BigDecimal.valueOf(1D), typeCoercer.coerce(1D, BigDecimal.class));
		assertEquals(Double.valueOf(1D), typeCoercer.coerce(BigDecimal.valueOf(1D), Double.class));
	}
	
	@Test
	public void testWithCoreAutoGenerateSourceContributions() {
		for (DatabaseVendor vendor : DatabaseVendor.values()) {
			testWithCoreAutoGenerateSourceContributions(vendor);
		}
	}

	private void testWithCoreAutoGenerateSourceContributions(DatabaseVendor vendor) {
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
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		List<Date> expectedDates = new ArrayList<Date>();
		expectedDates.add(cal.getTime());
		cal.add(Calendar.DATE, 1);
		expectedDates.add(cal.getTime());
		cal.add(Calendar.DATE, 1);
		expectedDates.add(cal.getTime());
		
		assertColumn(entities, "id", 0, 1, 2);
		assertColumn(entities, "intRequired", 0, 1, 2);
		assertNullColumn(entities, "intNullable");
		assertColumn(entities, "varcharRequired", "varcharRequired0", "varcharRequired1", "varcharRequired2");
		assertNullColumn(entities, "varcharNullable");
		assertColumn(entities, "blobRequired", "blobRequired0".getBytes(), "blobRequired1".getBytes(), "blobRequired2".getBytes());
		assertNullColumn(entities, "blobNullable");
		assertColumn(entities, "dateRequired", Date.class, expectedDates.toArray());
		assertNullColumn(entities, "dateNullable");
		assertColumn(entities, "timestampRequired", Date.class, expectedDates.toArray());
		assertNullColumn(entities, "timestampNullable");
	}

	private void assertNullColumn(List<Entity> entities, String propertyName) {
		for (Entity entity : entities) {
			assertNull(entity.getValue(propertyName));
		}
	}

	private void assertColumn(List<Entity> entities, String propertyName, Object... expected) {
		List<Object> actual = new ArrayList<Object>();
		for (Entity entity : entities) {
			actual.add(entity.getValue(propertyName));
		}
		assertEquals(Arrays.asList(expected), actual);
	}

	private void assertColumn(List<Entity> entities, String propertyName, Class<?> type, Object... expected) {
		List<Object> actual = new ArrayList<Object>();
		for (Entity entity : entities) {
			actual.add(entity.getValue(propertyName, type));
		}
		assertEquals(Arrays.asList(expected), actual);
	}
	
	private void assertColumn(List<Entity> entities, String propertyName, byte[]... expected) {
		assertEquals(entities.size(), expected.length);
		for (int i = 0; i < entities.size(); ++i) {
			Entity entity = entities.get(i);
			byte[] actual = entity.getValue(propertyName, byte[].class);
			assertArrayEquals(expected[i], actual);
		}
	}
	
}
