package org.tader;


public class LongIntegerTypeCoercerContribution extends TypeCoercerContribution<Long, Integer> {

	public LongIntegerTypeCoercerContribution() {
		super(Long.class, Integer.class);
	}
	
	@Override
	public Integer coerce(Long source) {
		return source.intValue();
	}
}
