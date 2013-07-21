package org.grater.model.internal;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.grater.model.ColumnModel;
import org.grater.model.Table;
import org.grater.model.TableModel;

public class TableModelImpl implements TableModel {
	private final String name;
	private final Table table;
	private final int order;
	
	private Map<String, ColumnModel> columns = new LinkedHashMap<String, ColumnModel>();
	private Map<String, ColumnModel> foreignKeys = new LinkedHashMap<String, ColumnModel>();
	private Set<ColumnModel> primaryKey;
	
	public TableModelImpl(String name, Table table, int order) {
		super();
		this.name = name;
		this.table = table;
		this.order = order;
	}
	
	public void addColumn(ColumnModel column) {
		columns.put(column.getName(), column);
	}
	
	public void addForeignKey(String column, ColumnModel foreignColumn) {
		columns.put(column, foreignColumn);
	}
	
	public void setPrimaryKey(Set<ColumnModel> columns) {
		this.primaryKey = columns;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public int getOrder() {
		return order;
	}
	
	@Override
	public Collection<ColumnModel> getColumns() {
		return columns.values();
	}
	
	@Override
	public ColumnModel getColumn(String name) {
		return columns.get(name);
	}
	
	@Override
	public boolean isPrimaryKey(String name) {
		return primaryKey.contains(name);
	}
	
	@Override
	public boolean isForeignKey(String name) {
		return foreignKeys.containsKey(name);
	}
	
	@Override
	public ColumnModel getForeignColumn(String name) {
		return foreignKeys.get(name);
	}
	
	@Override
	public Table getTable() {
		return table;
	}
	
	public Set<ColumnModel> getPrimaryKey() {
		return primaryKey;
	}
}
