package org.grater;

public interface AutoGenerateStrategy {
	Object generate(PropertyDef propertyDef, int increment);
}
