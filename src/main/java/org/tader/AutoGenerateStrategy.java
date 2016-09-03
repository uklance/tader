package org.tader;

public interface AutoGenerateStrategy {
	Object generate(PropertyDef propDef, int increment);
}
