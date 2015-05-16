package org.grater.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.grater.Entity;
import org.grater.EntityImpl;
import org.grater.EntityPersistence;
import org.grater.EntitySchema;
import org.grater.PropertyDef;
import org.grater.TypeCoercer;

public class JdbcEntityPersistence implements EntityPersistence {
	private final EntitySchema schema;
	private final JdbcTemplate jdbcTemplate;
	private final TypeCoercer typeCoercer;
	private final SelectHandlerSource selectHandlerSource;
	private final InsertHandlerSource insertHandlerSource;
	private final NameTranslator nameTranslator;

	public JdbcEntityPersistence(EntitySchema schema, JdbcTemplate jdbcTemplate, TypeCoercer typeCoercer, NameTranslator nameTranslator,
			SelectHandlerSource selectHandlerSource, InsertHandlerSource insertHandlerSource) {
		super();
		this.schema = schema;
		this.jdbcTemplate = jdbcTemplate;
		this.typeCoercer = typeCoercer;
		this.selectHandlerSource = selectHandlerSource;
		this.insertHandlerSource = insertHandlerSource;
		this.nameTranslator = nameTranslator;
	}

	@Override
	public Entity insert(final String entityName, final Map<String, Object> values) {
		ConnectionCallback<Object> callback = new ConnectionCallback<Object>() {
			@Override
			public Object handle(Connection con) throws SQLException {
				StringBuilder columnsSql = new StringBuilder();
				StringBuilder valuesSql = new StringBuilder();

				boolean isFirst = true;

				for (String propertyName : values.keySet()) {
					PropertyDef propDef = schema.getPropertyDef(entityName, propertyName);
					InsertHandler insertHandler = insertHandlerSource.get(propDef);
					insertHandler.onColumnsSql(columnsSql, isFirst, propDef);
					insertHandler.onValuesSql(valuesSql, isFirst, propDef);

					isFirst = false;
				}

				String table = nameTranslator.getTableForEntity(entityName);

				String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", table, columnsSql, valuesSql);

				PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

				int index = 1;
				for (Map.Entry<String, Object> entry : values.entrySet()) {
					String propertyName = entry.getKey();
					Object value = entry.getValue();
					PropertyDef propDef = schema.getPropertyDef(entityName, propertyName);
					InsertHandler insertHandler = insertHandlerSource.get(propDef);
					insertHandler.onPreparedStatement(ps, index++, propDef, value);
				}

				ps.executeUpdate();

				String pkPropName = schema.getPrimaryKeyPropertyName(entityName);
				Object pk = values.get(pkPropName);

				if (pk == null) {
					PropertyDef propDef = schema.getPropertyDef(entityName, pkPropName);
					if (propDef.isAutoIncrement() || propDef.isGenerated()) {
						InsertHandler insertHandler = insertHandlerSource.get(propDef);

						ResultSet rs = ps.getGeneratedKeys();
						pk = insertHandler.getGeneratedKey(rs, propDef);
					}
				}
				return pk;
			}
		};
		Object pk = jdbcTemplate.execute(callback);
		return get(entityName, pk);
	}

	@Override
	public Entity get(final String entityName, final Object primaryKey) {
		ConnectionCallback<Entity> callback = new ConnectionCallback<Entity>() {
			@Override
			public Entity handle(Connection con) throws SQLException {
				Collection<PropertyDef> propDefs = schema.getPropertyDefs(entityName);

				StringBuilder columnsSql = new StringBuilder();
				boolean isFirst = true;
				for (PropertyDef propDef : propDefs) {
					SelectHandler selectHandler = selectHandlerSource.get(propDef);
					selectHandler.onColumnsSql(columnsSql, isFirst, propDef);

					isFirst = false;
				}

				StringBuilder whereSql = new StringBuilder();

				String pkPropName = schema.getPrimaryKeyPropertyName(entityName);
				PropertyDef pkPropDef = schema.getPropertyDef(entityName, pkPropName);
				SelectHandler pkHandler = selectHandlerSource.get(pkPropDef);
				pkHandler.onWhereSql(whereSql, true, pkPropDef);

				String table = nameTranslator.getTableForEntity(entityName);
				String sql = String.format("SELECT %s FROM %s WHERE %s", columnsSql, table, whereSql);
				PreparedStatement ps = con.prepareStatement(sql);

				pkHandler.onPreparedStatement(ps, 1, pkPropDef, primaryKey);

				ResultSet rs = ps.executeQuery();

				rs.next();
				int index = 1;

				Map<String, Object> values = new LinkedHashMap<String, Object>();

				for (PropertyDef propDef : propDefs) {
					SelectHandler selectHandler = selectHandlerSource.get(propDef);
					Object value = selectHandler.getValue(rs, index++, propDef);

					values.put(propDef.getPropertyName(), value);
				}

				if (rs.next()) {
					throw new RuntimeException(String.format("Multiple rows found for %s with primary key %s", entityName, primaryKey));
				}

				return new EntityImpl(schema, JdbcEntityPersistence.this, typeCoercer, entityName, values);
			}
		};
		return jdbcTemplate.execute(callback);
	}
}
