package org.tader;

public class MutablePropertyDef implements PropertyDef {
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

	public MutablePropertyDef(String entityName, String tableName, String propertyName, String columnName) {
		super();
		this.entityName = entityName;
		this.tableName = tableName;
		this.propertyName = propertyName;
		this.columnName = columnName;
	}
	
	public MutablePropertyDef(PropertyDef propertyDef) {
		this(propertyDef.getEntityName(), propertyDef.getTableName(), propertyDef.getPropertyName(), propertyDef.getColumnName());

		setSqlType(propertyDef.getSqlType());
		setColumnSize(propertyDef.getColumnSize());
		setDecimalDigits(propertyDef.getDecimalDigits());
		setNullable(propertyDef.isNullable());
		setAutoIncrement(propertyDef.isAutoIncrement());
		setGenerated(propertyDef.isGenerated());
		setPrimaryKey(propertyDef.isPrimaryKey());
		setForeignKey(propertyDef.isForeignKey());
		setForeignEntityName(propertyDef.getForeignEntityName());
	}

	@Override
	public int getColumnSize() {
		return columnSize;
	}

	public void setColumnSize(int columnSize) {
		this.columnSize = columnSize;
	}

	@Override
	public int getDecimalDigits() {
		return decimalDigits;
	}

	public void setDecimalDigits(int decimalDigits) {
		this.decimalDigits = decimalDigits;
	}

	@Override
	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	@Override
	public boolean isAutoIncrement() {
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement) {
		this.autoIncrement = autoIncrement;
	}

	@Override
	public boolean isGenerated() {
		return generated;
	}

	public void setGenerated(boolean generated) {
		this.generated = generated;
	}

	@Override
	public String getEntityName() {
		return entityName;
	}

	@Override
	public String getTableName() {
		return tableName;
	}

	@Override
	public String getPropertyName() {
		return propertyName;
	}

	@Override
	public String getColumnName() {
		return columnName;
	}

	@Override
	public int getSqlType() {
		return sqlType;
	}

	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}
	
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	@Override
	public boolean isPrimaryKey() {
		return primaryKey;
	}
	
	public void setForeignKey(boolean foreignKey) {
		this.foreignKey = foreignKey;
	}
	
	@Override
	public String getForeignEntityName() {
		return foreignEntityName;
	}
	
	public void setForeignEntityName(String foreignEntityName) {
		this.foreignEntityName = foreignEntityName;
	}
	
	@Override
	public boolean isForeignKey() {
		return foreignKey;
	}
}
