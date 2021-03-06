package org.tader.autogen;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.tader.AutoGenerateStrategy;
import org.tader.PropertyDef;

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
		this(TimeZone.getDefault());
	}

	public DefaultDateAutoGenerateStrategy(TimeZone timeZone) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(timeZone);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		this.startsFrom = cal.getTime();
		this.incrementBy = 1;
		this.calendarField = Calendar.DATE;
	}

	@Override
	public Object generate(PropertyDef propDef, int increment) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startsFrom);
		cal.add(calendarField, increment * incrementBy);

		return cal.getTime();
	}
}
