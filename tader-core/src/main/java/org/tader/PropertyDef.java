package org.tader;

public interface PropertyDef {

	public int getColumnSize();

	public int getDecimalDigits();

	public boolean isNullable();

	public boolean isAutoIncrement();

	public boolean isGenerated();

	public String getEntityName();

	public String getTableName();

	public String getPropertyName();

	public String getColumnName();

	public int getSqlType();

	public boolean isPrimaryKey();

	public String getForeignEntityName();

	public boolean isForeignKey();

}