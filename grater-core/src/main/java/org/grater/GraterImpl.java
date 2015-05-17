package org.grater;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class GraterImpl implements Grater {
	private final EntitySchema schema;
	private final EntityPersistence persistence;
	private final AutoGenerateSource autoGenerateSource;
	
	public GraterImpl(EntitySchema schema, EntityPersistence persistence, AutoGenerateSource autoGenerateSource) {
		super();
		this.schema = schema;
		this.persistence = persistence;
		this.autoGenerateSource = autoGenerateSource;
	}

	@Override
	public Entity insert(PartialEntity partial) {
		String entityName = partial.getName();
		Set<String> autoPropNames = autoGenerateSource.getAutoGeneratePropertyNames(entityName);
		
		Map<String, Object> partialValues = partial.getValues();
		Set<String> providedPropNames = partialValues.keySet();

		Map<String, Object> unresolvedValues = new LinkedHashMap<String, Object>(partialValues);
		
		Integer increment = null;
		for (String autoPropName : autoPropNames) {
			if (!providedPropNames.contains(autoPropName)) {
				PropertyDef propDef = schema.getPropertyDef(entityName, autoPropName);
				AutoGenerateStrategy autoStrategy = autoGenerateSource.getAutoGenerateStrategy(propDef);
				if (increment == null) {
					increment = autoGenerateSource.getNextIncrement(entityName);
				}
				Object autoValue = autoStrategy.generate(propDef, increment);
				unresolvedValues.put(autoPropName, autoValue);
			}
		}
		
		Map<String, Object> resolvedValues = new LinkedHashMap<String, Object>();
		for (Map.Entry<String, Object> entry : unresolvedValues.entrySet()) {
			String propName = entry.getKey();
			Object unresolved = entry.getValue();
			Object resolved;
			if (unresolved instanceof PartialEntity) {
				// recursively insert any partial entities
				Entity inserted = insert((PartialEntity) unresolved);
				resolved = inserted.getPrimaryKey();
			} else if (unresolved instanceof Entity) {
				// use primary key for any entities
				resolved = ((Entity) unresolved).getPrimaryKey();
			} else {
				// normal case, use the provided value
				resolved = unresolved;
			}
			resolvedValues.put(propName, resolved);
		}
		
		Object primaryKey = persistence.insert(entityName, resolvedValues);
		return persistence.get(entityName, primaryKey);
	}
}
