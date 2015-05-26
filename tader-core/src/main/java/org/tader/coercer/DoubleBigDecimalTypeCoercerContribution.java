package org.tader.coercer;

import java.math.BigDecimal;

import org.tader.TypeCoercerContribution;


public class DoubleBigDecimalTypeCoercerContribution extends TypeCoercerContribution<Double, BigDecimal> {

	public DoubleBigDecimalTypeCoercerContribution() {
		super(Double.class, BigDecimal.class);
	}

	@Override
	public BigDecimal coerce(Double source) {
		return BigDecimal.valueOf(source);
	}
}
