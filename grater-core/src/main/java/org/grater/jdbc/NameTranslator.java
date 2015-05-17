package org.grater.jdbc;

public interface NameTranslator {
	String getTableForEntity(String entityName);
	String getPropertyForColumn(String tableName, String columnName);
	String getEntityForTable(String tableName);
}
