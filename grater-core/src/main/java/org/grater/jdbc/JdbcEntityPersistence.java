package org.grater.jdbc;

import java.util.Map;

import org.grater.Entity;
import org.grater.EntityPersistence;

public class JdbcEntityPersistence implements EntityPersistence {

	@Override
	public Entity insert(String entityName, Map<String, Object> value) {
		throw new UnsupportedOperationException("JdbcEntityPersistence.insert");
	}

	@Override
	public Entity get(String entityName, Object primaryKey) {
		throw new UnsupportedOperationException("JdbcEntityPersistence.get");
	}

}
