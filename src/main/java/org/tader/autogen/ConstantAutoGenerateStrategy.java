package org.tader.autogen;

import org.tader.AutoGenerateStrategy;
import org.tader.PropertyDef;

public class ConstantAutoGenerateStrategy implements AutoGenerateStrategy {
	private final Object value;
	
	public ConstantAutoGenerateStrategy(Object value) {
		super();
		this.value = value;
	}



	@Override
	public Object generate(PropertyDef propDef, int increment) {
		return value;
	}
}
