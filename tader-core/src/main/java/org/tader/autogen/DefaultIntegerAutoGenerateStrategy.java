package org.tader.autogen;

import org.tader.AutoGenerateStrategy;
import org.tader.PropertyDef;


public class DefaultIntegerAutoGenerateStrategy implements AutoGenerateStrategy {
	private final int startsFrom;
	private final int incrementBy;

	public DefaultIntegerAutoGenerateStrategy() {
		this(0, 1);
	}
	
	public DefaultIntegerAutoGenerateStrategy(int startsFrom, int incrementBy) {
		super();
		this.startsFrom = startsFrom;
		this.incrementBy = incrementBy;
	}

	@Override
	public Object generate(PropertyDef propDef, int increment) {
		return (increment * incrementBy) + startsFrom;
	}
}
