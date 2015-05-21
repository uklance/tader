package org.tader.jdbc;

public class NoopNameTranslator implements NameTranslator {

	@Override
	public String getTableForEntity(String entityName) {
		return entityName;
	}

	@Override
	public String getPropertyForColumn(String tableName, String columnName) {
		return columnName;
	}
	
	@Override
	public String getEntityForTable(String tableName) {
		return tableName;
	}
}
