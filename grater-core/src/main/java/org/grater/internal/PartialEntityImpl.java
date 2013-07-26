package org.grater.internal;

import java.util.Map;

import org.grater.PartialEntity;

public class PartialEntityImpl implements PartialEntity {
	private final String table;
	private final Map<String, Object> values;
	
	public PartialEntityImpl(String table, Map<String, Object> values) {
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
