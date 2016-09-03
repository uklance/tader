package org.tader.autogen;

import org.tader.AutoGenerateStrategy;
import org.tader.PropertyDef;


public class DefaultBlobAutoGenerateStrategy implements AutoGenerateStrategy {
	private final DefaultStringAutoGenerateStrategy stringStrategy;

	public DefaultBlobAutoGenerateStrategy() {
		stringStrategy = new DefaultStringAutoGenerateStrategy();
	}

	@Override
	public Object generate(PropertyDef propDef, int increment) {
		String stringValue = (String) stringStrategy.generate(propDef, increment);
		return stringValue.getBytes();
	}
}
