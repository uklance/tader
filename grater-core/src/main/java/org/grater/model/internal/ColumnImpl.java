package org.grater.model.internal;

import org.grater.model.Column;

public class ColumnImpl implements Column {
	private final String name;
	private final int type;
	private final boolean nullable;
	private final int size;
	
	public ColumnImpl(String name, int type, boolean nullable, int size) {
		super();
		this.name = name;
		this.type = type;
		this.nullable = nullable;
		this.size = size;
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
	public int getSize() {
		return size;
	}
	@Override
	public String toString() {
		return String.format("Column[%s]", name);
	}
	
}
