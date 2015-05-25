package org.tader;

import java.util.Calendar;
import java.util.Date;


public class DefaultDateAutoGenerateStrategy implements AutoGenerateStrategy {
	private final Date startsFrom;
	private final int incrementBy;
	private final int calendarField;
	
	public DefaultDateAutoGenerateStrategy(Date startsFrom, int incrementBy, int calendarField) {
		super();
		this.startsFrom = startsFrom;
		this.incrementBy = incrementBy;
		this.calendarField = calendarField;
	}
	
	public DefaultDateAutoGenerateStrategy() {
		this(new Date(), 1, Calendar.DATE);
	}

	@Override
	public Object generate(PropertyDef propDef, int increment) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startsFrom);
		cal.add(calendarField, increment * incrementBy);
		
		return cal.getTime();
	}
}
