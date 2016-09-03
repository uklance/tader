package org.tader.builder;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;
import org.tader.TypeCoercer;

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
}
