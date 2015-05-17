package org.grater;

import java.util.Collection;
import java.util.Map;

public class EntityImpl implements Entity {
	private final String entityName;
	private final Map<String, Object> values;
	private final EntitySchema schema;
	private final EntityPersistence entityPersistence;
	private final TypeCoercer typeCoercer;
	
	public EntityImpl(EntitySchema schema, EntityPersistence entityPersistence, TypeCoercer typeCoercer, String entityName, Map<String, Object> values) {
		this.entityName = entityName;
		this.values = values;
		this.schema = schema;
		this.entityPersistence = entityPersistence;
		this.typeCoercer = typeCoercer;
	}

	@Override
	public Collection<String> getPropertyNames() {
		return values.keySet();
	}

	@Override
	public Object getPrimaryKey() {
		String pkProp = schema.getPrimaryKeyPropertyName(entityName);
		return values.get(pkProp);
	}

	@Override
	public Entity getEntity(String propertyName) {
		PropertyDef propDef = schema.getPropertyDef(entityName, propertyName);
		if (!propDef.isForeignKey()) {
			throw new RuntimeException(propertyName + " is not a foreign key");
		}
		Object value = values.get(propertyName);
		return entityPersistence.get(propDef.getForeignEntityName(), value);
	}

	@Override
	public Object getValue(String propertyName) {
		if (!values.containsKey(propertyName)) {
			throw new RuntimeException("No such property " + propertyName);
		}
		return values.get(propertyName);
	}

	@Override
	public <T> T getValue(String propertyName, Class<T> type) {
		return typeCoercer.coerce(getValue(propertyName), type);
	}
	
	@Override
	public String getString(String propertyName) {
		return getValue(propertyName, String.class);
	}
	
	@Override
	public Long getLong(String propertyName) {
		return getValue(propertyName, Long.class);
	}
	
	@Override
	public Integer getInteger(String propertyName) {
		return getValue(propertyName, Integer.class);
	}
}
