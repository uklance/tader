package org.grater.model.internal;

import org.grater.model.Column;
import org.grater.model.ColumnModel;
import org.grater.model.TableModel;

public class ColumnModelImpl implements ColumnModel {
	private final TableModel table;
	private final Column column;
	private final String name;
	
	public ColumnModelImpl(TableModel table, Column column, String name) {
		super();
		this.table = table;
		this.column = column;
		this.name = name;
	}
	
	@Override
	public TableModel getTable() {
		return table;
	}
	
	@Override
	public Column getColumn() {
		return column;
	}
	
	@Override
	public String getName() {
		return name;
	}
}
