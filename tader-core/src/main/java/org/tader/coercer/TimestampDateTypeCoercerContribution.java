package org.tader.coercer;

import java.sql.Timestamp;
import java.util.Date;

import org.tader.TypeCoercerContribution;


public class TimestampDateTypeCoercerContribution extends TypeCoercerContribution<Timestamp, Date> {
	public TimestampDateTypeCoercerContribution() {
		super(Timestamp.class, Date.class);
	}
	
	@Override
	public Date coerce(Timestamp source) {
		return new Date(source.getTime());
	}
}
