package org.grater;

public interface AutoGenerateStrategy {
	Object generate(PropertyDef propDef, int increment);
}
