package org.tader;

public class DefaultStringAutoGenerateStrategy implements AutoGenerateStrategy {
	private int startsFrom;
	
	public DefaultStringAutoGenerateStrategy(int startsFrom) {
		super();
		this.startsFrom = startsFrom;
	}

	public DefaultStringAutoGenerateStrategy() {
		this(0);
	}

	@Override
	public Object generate(PropertyDef propDef, int increment) {
		int suffix = increment + startsFrom;
		return propDef.getPropertyName() + suffix;
	}
}
