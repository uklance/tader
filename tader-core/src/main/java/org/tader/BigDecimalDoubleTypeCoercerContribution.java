package org.tader;

import java.math.BigDecimal;


public class BigDecimalDoubleTypeCoercerContribution extends TypeCoercerContribution<BigDecimal, Double> {

	public BigDecimalDoubleTypeCoercerContribution() {
		super(BigDecimal.class, Double.class);
	}

	@Override
	public Double coerce(BigDecimal source) {
		return source.doubleValue();
	}
}
