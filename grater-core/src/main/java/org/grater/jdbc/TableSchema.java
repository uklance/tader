package org.grater.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.grater.PropertyDef;

public class TableSchema {
	private final String entityName;
	private final Map<String, PropertyDef> propertyDefs;
	private final String primaryKeyPropertyName;

	public TableSchema(String entityName, Collection<PropertyDef> propDefs) {
		Map<String, PropertyDef> map = new LinkedHashMap<String, PropertyDef>(propDefs.size());
		List<String> pkPropNames = new ArrayList<String>();
		for (PropertyDef propertyDef : propDefs) {
			if (propertyDef.isPrimaryKey()) {
				pkPropNames.add(propertyDef.getPropertyName());
			}
			map.put(propertyDef.getPropertyName(), propertyDef);
		}
		if (pkPropNames.size() != 1) {
			throw new RuntimeException(String.format("Found %s primary key columns for %s, expecting 1", pkPropNames.size(), entityName));
		}
		this.entityName = entityName;
		this.propertyDefs = Collections.unmodifiableMap(map);
		this.primaryKeyPropertyName = pkPropNames.get(0);
	}

	public Collection<PropertyDef> getPropertyDefs() {
		return propertyDefs.values();
	}

	public String getPrimaryKeyPropertyName() {
		return primaryKeyPropertyName;
	}

	public String getEntityName() {
		return entityName;
	}

	public PropertyDef getPropertyDef(String propertyName) {
		PropertyDef propDef = propertyDefs.get(propertyName);
		if (propDef == null) {
			throw new RuntimeException(String.format("No such property %s.%s", entityName, propertyName));
		}
		return propDef;
	}
}
