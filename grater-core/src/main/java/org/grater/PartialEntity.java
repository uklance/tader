package org.grater;

import java.util.LinkedHashMap;
import java.util.Map;

public class PartialEntity {
	private final String name;
	private Map<String, Object> values = new LinkedHashMap<String, Object>();

	public PartialEntity(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public PartialEntity withValue(String propertyName, Object value) {
		values.put(propertyName, value);
		return this;
	}

	public Object getValue(String propertyName) {
		return values.get(propertyName);
	}

	public Map<String, Object> getValues() {
		return values;
	}
}
