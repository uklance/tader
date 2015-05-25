package org.tader;

import java.sql.Timestamp;
import java.util.Date;


public class TimestampDateTypeCoercerContribution extends TypeCoercerContribution<Timestamp, Date> {
	public TimestampDateTypeCoercerContribution() {
		super(Timestamp.class, Date.class);
	}
	
	@Override
	public Date coerce(Timestamp source) {
		return new Date(source.getTime());
	}
}
