package org.grater;

import java.util.Map;

public interface Grater {
	Entity insert(PartialEntity row);
	Entity insert(String table, Object... columnsAndValues);
	Entity insert(String table, Map<String, Object> fields);
	Entity select(String table, Object pk);
	Entity select(String table, Map<String, Object> pk);
    
	/*
	<T extends Entity> insert(Class<T> type, Map fields);
	<T extends Entity> T insert(T fields);
	boolean delete(String table, Object pk);
	boolean delete(Entity entity);
	int deleteAll(String table, Map fields);
	int deleteAll(Entity fields);
	*/
}
