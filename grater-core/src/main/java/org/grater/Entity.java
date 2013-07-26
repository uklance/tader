package org.grater;

import java.util.Map;

public interface Entity {
	String getTable();
	Map<String, Object> getValues();
	String getString(String name);
	Integer getInteger(String name);
	<T> T get(String name, Class<T> type);
	Entity getEntity(String name);
}
