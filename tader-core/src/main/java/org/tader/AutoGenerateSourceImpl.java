package org.tader;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoGenerateSourceImpl implements AutoGenerateSource {
	private final ConcurrentMap<String, AtomicInteger> nextIncrementByEntity = new ConcurrentHashMap<String, AtomicInteger>();
	private final EntitySchema schema;
	private final Map<String, Map<String, AutoGenerateStrategy>> strategiesByEntityByProperty = new LinkedHashMap<String, Map<String, AutoGenerateStrategy>>();
	private final Map<Integer, AutoGenerateStrategy> strategiesBySqlType = new LinkedHashMap<Integer, AutoGenerateStrategy>();

	private static final AutoGenerateStrategy FOREIGN_KEY_STRATEGY = new AutoGenerateStrategy() {
		@Override
		public Object generate(PropertyDef propDef, int increment) {
			return new PartialEntity(propDef.getForeignEntityName());
		}
	};

	public AutoGenerateSourceImpl(EntitySchema schema, Collection<AutoGenerateSourceContribution> contributions) {
		super();
		this.schema = schema;

		// TODO: handle duplicates
		for (AutoGenerateSourceContribution contribution : contributions) {
			for (Map.Entry<String, Map<String, AutoGenerateStrategy>> entry : contribution.getStrategiesByEntityByProperty().entrySet()) {
				Map<String, AutoGenerateStrategy> strategiesByProperty = strategiesByEntityByProperty.get(entry.getKey());
				if (strategiesByProperty == null) {
					strategiesByProperty = new LinkedHashMap<String, AutoGenerateStrategy>();
					strategiesByEntityByProperty.put(entry.getKey(), strategiesByProperty);
				}
				strategiesByProperty.putAll(entry.getValue());
			}
			strategiesBySqlType.putAll(contribution.getStrategiesBySqlType());
		}
	}

	@Override
	public Set<String> getAutoGeneratePropertyNames(String entityName) {
		Set<String> autoPropNames = new LinkedHashSet<String>();
		Map<String, AutoGenerateStrategy> entityStrategies = strategiesByEntityByProperty.get(entityName);
		for (PropertyDef propDef : schema.getPropertyDefs(entityName)) {
			String propName = propDef.getPropertyName();
			boolean isAuto = entityStrategies != null && entityStrategies.containsKey(propName);
			if (!isAuto) {
				isAuto = !propDef.isNullable() && !propDef.isGenerated() && !propDef.isAutoIncrement();
			}
			if (isAuto) {
				autoPropNames.add(propName);
			}
		}
		return autoPropNames;
	}

	@Override
	public int getNextIncrement(String entityName) {
		AtomicInteger nextIncrement = nextIncrementByEntity.get(entityName);
		if (nextIncrement == null) {
			AtomicInteger candidate = new AtomicInteger(1);
			AtomicInteger previous = nextIncrementByEntity.putIfAbsent(entityName, candidate);

			nextIncrement = previous == null ? candidate : previous;
		}
		return nextIncrement.getAndIncrement();
	}

	@Override
	public AutoGenerateStrategy getAutoGenerateStrategy(PropertyDef propDef) {
		String entityName = propDef.getEntityName();
		String propertyName = propDef.getPropertyName();
		Map<String, AutoGenerateStrategy> entityStrategies = strategiesByEntityByProperty.get(entityName);
		if (entityStrategies != null && entityStrategies.containsKey(propertyName)) {
			return entityStrategies.get(propertyName);
		}
		if (propDef.isForeignKey()) {
			return FOREIGN_KEY_STRATEGY;
		}

		AutoGenerateStrategy strategy = strategiesBySqlType.get(propDef.getSqlType());
		if (strategy == null) {
			throw new RuntimeException(String.format("No AutoGenerateStrategy configured for %s.%s (sqlType=%s)", 
					entityName, propertyName, propDef.getSqlType()));
		}
		return strategy;
	}
}
