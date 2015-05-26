package org.tader.coercer;

import java.math.BigDecimal;

import org.tader.TypeCoercerContribution;


public class BigDecimalDoubleTypeCoercerContribution extends TypeCoercerContribution<BigDecimal, Double> {

	public BigDecimalDoubleTypeCoercerContribution() {
		super(BigDecimal.class, Double.class);
	}

	@Override
	public Double coerce(BigDecimal source) {
		return source.doubleValue();
	}
}
