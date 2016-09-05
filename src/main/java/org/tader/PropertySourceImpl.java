package org.tader;

import java.util.Map;

public class PropertySourceImpl implements PropertySource {
	private Map<String, String> properties;
	
	public PropertySourceImpl(Map<String, String> properties) {
		super();
		this.properties = properties;
	}

	@Override
	public String getProperty(String name) {
		if (!properties.containsKey(name)) throw new IllegalArgumentException("No such property " + name);
		return properties.get(name);
	}
}
