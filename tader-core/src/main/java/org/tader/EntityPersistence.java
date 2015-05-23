package org.tader;

import java.util.Map;

public interface EntityPersistence {
	/**
	 * @param entityName
	 * @param values
	 * @return The primary key of the inserted entity
	 */
	Object insert(String entityName, Map<String, Object> values);
	Entity get(String entityName, Object primaryKey);
	void delete(String name, Object primaryKey);
}
