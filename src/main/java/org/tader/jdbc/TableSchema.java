package org.tader.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.tader.PropertyDef;

public class TableSchema {
	private final String entityName;
	private final Map<String, PropertyDef> propertyDefs;
	private final int pkColumnCount;
	private final String primaryKeyPropertyName;

	public TableSchema(String entityName, Collection<? extends PropertyDef> propDefs) {
		Map<String, PropertyDef> map = new LinkedHashMap<String, PropertyDef>(propDefs.size());
		List<String> pkPropNames = new ArrayList<String>();
		for (PropertyDef propertyDef : propDefs) {
			if (propertyDef.isPrimaryKey()) {
				pkPropNames.add(propertyDef.getPropertyName());
			}
			map.put(propertyDef.getPropertyName(), propertyDef);
		}
		this.entityName = entityName;
		this.propertyDefs = Collections.unmodifiableMap(map);
		this.pkColumnCount = pkPropNames.size();
		this.primaryKeyPropertyName = (pkColumnCount == 1) ? pkPropNames.get(0) : null;
	}

	public Collection<PropertyDef> getPropertyDefs() {
		return propertyDefs.values();
	}

	public String getPrimaryKeyPropertyName() {
		if (pkColumnCount != 1) {
			throw new RuntimeException(String.format("Found %s primary key columns for %s, expecting 1", pkColumnCount, entityName));
		}
		return primaryKeyPropertyName;
	}

	public PropertyDef getPropertyDef(String propertyName) {
		PropertyDef propDef = propertyDefs.get(propertyName);
		if (propDef == null) {
			throw new RuntimeException(String.format("No such property %s.%s %s", entityName, propertyName, propertyDefs.keySet()));
		}
		return propDef;
	}
}
