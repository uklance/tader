package org.tader.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.tader.Entity;
import org.tader.EntityImpl;
import org.tader.EntityPersistence;
import org.tader.EntitySchema;
import org.tader.PropertyDef;
import org.tader.TypeCoercer;

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
	public Object insert(final String entityName, final Map<String, Object> values) {
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
						rs.next();
						pk = insertHandler.getGeneratedKey(rs, propDef);
					}
				}
				return pk;
			}
		};
		Object pk = jdbcTemplate.execute(callback);
		return pk;
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
	
	@Override
	public void delete(final String entityName, final Object primaryKey) {
		ConnectionCallback<Void> callback = new ConnectionCallback<Void>() {
			@Override
			public Void handle(Connection con) throws SQLException {
				String table = nameTranslator.getTableForEntity(entityName);
				StringBuilder whereSql = new StringBuilder();
				PropertyDef propDef = schema.getPropertyDef(entityName, schema.getPrimaryKeyPropertyName(entityName));
				SelectHandler selectHandler = selectHandlerSource.get(propDef);
				selectHandler.onWhereSql(whereSql, true, propDef);
				String sql = String.format("DELETE FROM %s WHERE %s", table, whereSql);
				
				PreparedStatement ps = con.prepareStatement(sql);
				selectHandler.onPreparedStatement(ps, 1, propDef, primaryKey);
				
				int result = ps.executeUpdate();
				
				if (result != 1) {
					throw new RuntimeException(result + " rows deleted, expected 1");
				}
				return null;
			}
		};
		jdbcTemplate.execute(callback);
	}
}
