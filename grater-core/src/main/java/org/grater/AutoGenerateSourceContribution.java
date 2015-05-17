package org.grater;

import java.util.LinkedHashMap;
import java.util.Map;

public class AutoGenerateSourceContribution {
	private final Map<String, Map<String, AutoGenerateStrategy>> strategiesByEntityByProperty = new LinkedHashMap<String, Map<String, AutoGenerateStrategy>>();
	private final Map<Integer, AutoGenerateStrategy> strategiesBySqlType = new LinkedHashMap<Integer, AutoGenerateStrategy>();

	public AutoGenerateSourceContribution withAutoGenerateStrategy(String entityName, String propertyName, AutoGenerateStrategy strategy) {
		Map<String, AutoGenerateStrategy> strategiesByProperty = strategiesByEntityByProperty.get(entityName);
		if (strategiesByProperty == null) {
			strategiesByProperty = new LinkedHashMap<String, AutoGenerateStrategy>();
			strategiesByEntityByProperty.put(entityName, strategiesByProperty);
		}
		strategiesByProperty.put(propertyName, strategy);
		return this;
	}
	
	public AutoGenerateSourceContribution withAutoGenerateStrategy(int sqlType, AutoGenerateStrategy strategy) {
		strategiesBySqlType.put(sqlType, strategy);
		return this;
	}
	
	public Map<String, Map<String, AutoGenerateStrategy>> getStrategiesByEntityByProperty() {
		return strategiesByEntityByProperty;
	}
	
	public Map<Integer, AutoGenerateStrategy> getStrategiesBySqlType() {
		return strategiesBySqlType;
	}
}
