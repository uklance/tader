package org.grater.internal;

import java.util.Map;

import org.grater.TableRow;

public class TableRowImpl implements TableRow {
	private final String table;
	private final Map<String, Object> values;
	
	public TableRowImpl(String table, Map<String, Object> values) {
		super();
		this.table = table;
		this.values = values;
	}
	
	public String getTable() {
		return table;
	}
	public Map<String, Object> getValues() {
		return values;
	}
}
