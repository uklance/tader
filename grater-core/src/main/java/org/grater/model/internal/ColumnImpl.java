package org.grater.model.internal;

import org.grater.model.Column;

public class ColumnImpl implements Column {
	private final String name;
	private final int type;
	private final boolean nullable;
	private final int scale;
	private final int precision;
	
	public ColumnImpl(String name, int type, boolean nullable, int scale, int precision) {
		super();
		this.name = name;
		this.type = type;
		this.nullable = nullable;
		this.scale = scale;
		this.precision = precision;
	}
	
	public String getName() {
		return name;
	}
	public int getType() {
		return type;
	}
	public boolean isNullable() {
		return nullable;
	}
	public int getScale() {
		return scale;
	}
	public int getPrecision() {
		return precision;
	}
}
