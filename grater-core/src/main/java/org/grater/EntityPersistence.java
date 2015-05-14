package org.grater;

import java.util.Map;

public interface EntityPersistence {
	Entity insert(String entityName, Map<String, Object> values);
	Entity get(String entityName, Object primaryKey);
}
