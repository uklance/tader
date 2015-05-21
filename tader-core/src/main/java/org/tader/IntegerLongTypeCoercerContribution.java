package org.tader;

import org.tader.jdbc.TypeCoercerContribution;

public class IntegerLongTypeCoercerContribution extends TypeCoercerContribution<Integer, Long> {
	public IntegerLongTypeCoercerContribution() {
		super(Integer.class, Long.class);
	}
	@Override
	public Long coerce(Integer source) {
		return Long.valueOf(source);
	}

}
