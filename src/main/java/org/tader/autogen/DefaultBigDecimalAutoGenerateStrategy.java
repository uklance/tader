package org.tader.autogen;

import java.math.BigDecimal;

import org.tader.AutoGenerateStrategy;
import org.tader.PropertyDef;

public class DefaultBigDecimalAutoGenerateStrategy implements AutoGenerateStrategy {
	private final BigDecimal startsAt;
	private final BigDecimal incrementBy;
	
	public DefaultBigDecimalAutoGenerateStrategy(BigDecimal startsAt, BigDecimal incrementBy) {
		super();
		this.startsAt = startsAt;
		this.incrementBy = incrementBy;
	}
	
	public DefaultBigDecimalAutoGenerateStrategy() {
		this(BigDecimal.ZERO, BigDecimal.ONE);
	}
	
	@Override
	public Object generate(PropertyDef propDef, int increment) {
		return incrementBy.multiply(BigDecimal.valueOf(increment)).add(startsAt);
	}
}
