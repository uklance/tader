package org.tader;

public class DefaultStringAutoGenerateStrategy implements AutoGenerateStrategy {

	@Override
	public Object generate(PropertyDef propDef, int increment) {
		return propDef.getPropertyName() + increment;
	}
}
