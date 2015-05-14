package org.grater;

public class PropertyDef {
	private final String entityName;
	private final String tableName;
	private final String propertyName;
	private final String columnName;
	private int sqlType;
	public PropertyDef(String entityName, String tableName, String propertyName, String columnName) {
		super();
		this.entityName = entityName;
		this.tableName = tableName;
		this.propertyName = propertyName;
		this.columnName = columnName;
	}
	public String getEntityName() {
		return entityName;
	}
	public String getTableName() {
		return tableName;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public String getColumnName() {
		return columnName;
	}
	public int getSqlType() {
		return sqlType;
	}
	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}
}
