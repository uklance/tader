package org.grater.internal;

import java.util.Map;

import org.grater.Entity;

public class EntityImpl implements Entity {
	private final String table;
	private final Map<String, Object> values;

	public EntityImpl(String table, Map<String, Object> values) {
		super();
		this.table = table;
		this.values = values;
	}

	@Override
	public String getTable() {
		return table;
	}

	@Override
	public Map<String, Object> getValues() {
		return values;
	}

	@Override
	public <T> T get(String name, Class<T> type) {
		return type.cast(values.get(name));
	}

	@Override
	public Integer getInteger(String name) {
		return get(name, Integer.class);
	}

	@Override
	public String getString(String name) {
		return get(name, String.class);
	}
	
	@Override
	public Entity getEntity(String name) {
		return get(name, Entity.class);
	}
}
