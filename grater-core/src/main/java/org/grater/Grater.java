package org.grater;

import java.util.Map;

public interface Grater {
	TableRow insert(TableRow row);
	TableRow insert(String table, Map<String, Object> fields);
	TableRow insert(String table, Object... columnsAndValues);
	TableRow select(String name, Map<String, Object> values);
    
	/*
	<T extends Entity> insert(Class<T> type, Map fields);
	<T extends Entity> T insert(T fields);
	boolean delete(String table, Object pk);
	boolean delete(Entity entity);
	int deleteAll(String table, Map fields);
	int deleteAll(Entity fields);
	*/
}
