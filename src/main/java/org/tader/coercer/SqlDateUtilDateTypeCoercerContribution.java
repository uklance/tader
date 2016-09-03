package org.tader.coercer;

import java.util.Date;

import org.tader.TypeCoercerContribution;


public class SqlDateUtilDateTypeCoercerContribution extends TypeCoercerContribution<java.sql.Date, java.util.Date> {
	public SqlDateUtilDateTypeCoercerContribution() {
		super(java.sql.Date.class, java.util.Date.class);
	}

	@Override
	public Date coerce(java.sql.Date source) {
		return new java.util.Date(source.getTime());
	}
}
