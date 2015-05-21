package org.tader;

import java.util.Collection;

public interface EntitySchema {
	Collection<PropertyDef> getPropertyDefs(String entityName);
	String getPrimaryKeyPropertyName(String entityName);
	PropertyDef getPropertyDef(String entityName, String propertyName);
}
