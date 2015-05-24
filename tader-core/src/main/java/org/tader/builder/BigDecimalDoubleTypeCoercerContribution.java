package org.tader.builder;

import java.math.BigDecimal;

import org.tader.jdbc.TypeCoercerContribution;

public class BigDecimalDoubleTypeCoercerContribution extends TypeCoercerContribution<BigDecimal, Double> {

	public BigDecimalDoubleTypeCoercerContribution() {
		super(BigDecimal.class, Double.class);
	}

	@Override
	public Double coerce(BigDecimal source) {
		return source.doubleValue();
	}
}
