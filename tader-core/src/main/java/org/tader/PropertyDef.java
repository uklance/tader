package org.tader;

public class PropertyDef {
	private final String entityName;
	private final String tableName;
	private final String propertyName;
	private final String columnName;
	private int sqlType;
	private int columnSize;
	private int decimalDigits;
	private boolean nullable;
	private boolean autoIncrement;
	private boolean generated;
	private boolean primaryKey;
	private boolean foreignKey;
	private String foreignEntityName;

	public PropertyDef(String entityName, String tableName, String propertyName, String columnName) {
		super();
		this.entityName = entityName;
		this.tableName = tableName;
		this.propertyName = propertyName;
		this.columnName = columnName;
	}


	public int getColumnSize() {
		return columnSize;
	}

	public void setColumnSize(int columnSize) {
		this.columnSize = columnSize;
	}

	public int getDecimalDigits() {
		return decimalDigits;
	}

	public void setDecimalDigits(int decimalDigits) {
		this.decimalDigits = decimalDigits;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	public boolean isGenerated() {
		return generated;
	}

	public void setGenerated(boolean generated) {
		this.generated = generated;
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
	
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	public boolean isPrimaryKey() {
		return primaryKey;
	}
	
	public void setForeignKey(boolean foreignKey) {
		this.foreignKey = foreignKey;
	}
	
	public String getForeignEntityName() {
		return foreignEntityName;
	}
	
	public void setForeignEntityName(String foreignEntityName) {
		this.foreignEntityName = foreignEntityName;
	}
	
	public boolean isForeignKey() {
		return foreignKey;
	}
}
