package org.grater;

import java.util.Collection;

public interface Entity {
	Collection<String> getPropertyNames();
	Object getPrimaryKey();
	Entity getEntity(String propertyName);
	<T> T getValue(String propertyName, Class<T> type);
	Object getValue(String propertyName);
	String getString(String propertyName);
}