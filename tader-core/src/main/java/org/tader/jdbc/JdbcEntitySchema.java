package org.tader.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.tader.EntitySchema;
import org.tader.PropertyDef;

public class JdbcEntitySchema implements EntitySchema {
	private final JdbcTemplate jdbcTemplate;
	private final NameTranslator nameTranslator;
	private final Map<String, TableSchema> tableSchemas = new ConcurrentHashMap<String, TableSchema>();

	public JdbcEntitySchema(JdbcTemplate jdbcTemplate, NameTranslator nameTranslator) {
		super();
		this.jdbcTemplate = jdbcTemplate;
		this.nameTranslator = nameTranslator;
	}

	@Override
	public Collection<PropertyDef> getPropertyDefs(String entityName) {
		return getTableSchema(entityName).getPropertyDefs();
	}

	@Override
	public String getPrimaryKeyPropertyName(String entityName) {
		return getTableSchema(entityName).getPrimaryKeyPropertyName();
	}
	
	@Override
	public PropertyDef getPropertyDef(String entityName, String propertyName) {
		return getTableSchema(entityName).getPropertyDef(propertyName);
	}

	protected TableSchema getTableSchema(String entityName) {
		TableSchema tableSchema = tableSchemas.get(entityName);
		if (tableSchema == null) {
			tableSchema = createTableSchema(entityName);
			tableSchemas.put(entityName, tableSchema);
		}
		return tableSchema;
	}

	protected TableSchema createTableSchema(final String entityName) {
		ConnectionCallback<TableSchema> callback = new ConnectionCallback<TableSchema>() {
			@Override
			public TableSchema handle(Connection con) throws SQLException {
				DatabaseMetaData meta = con.getMetaData();

				String tableName = nameTranslator.getTableForEntity(entityName);
				Map<String, PropertyDef> propertyDefs = new LinkedHashMap<String, PropertyDef>();
				ResultSet rs1 = meta.getColumns(null, null, tableName, null);
				while (rs1.next()) {
					String columnName = rs1.getString("COLUMN_NAME");
					String propertyName = nameTranslator.getPropertyForColumn(tableName, columnName);

					PropertyDef propertyDef = new PropertyDef(entityName, tableName, propertyName, columnName);

					propertyDef.setSqlType(rs1.getInt("DATA_TYPE"));
					propertyDef.setColumnSize(rs1.getInt("COLUMN_SIZE"));
					propertyDef.setDecimalDigits(rs1.getInt("DECIMAL_DIGITS"));
					propertyDef.setNullable("YES".equals(getSafeString(rs1, "IS_NULLABLE")));
					propertyDef.setAutoIncrement("YES".equals(getSafeString(rs1, "IS_AUTOINCREMENT")));
					propertyDef.setGenerated("YES".equals(getSafeString(rs1, "IS_GENERATEDCOLUMN")));

					propertyDefs.put(columnName, propertyDef);
				}
				rs1.close();
				if (propertyDefs.isEmpty()) {
					throw new RuntimeException("No columms for table " + tableName);
				}

				ResultSet rs2 = meta.getPrimaryKeys(null, null, tableName);
				while (rs2.next()) {
					String columnName = rs2.getString("COLUMN_NAME");
					PropertyDef propertyDef = propertyDefs.get(columnName);
					propertyDef.setPrimaryKey(true);
				}
				rs2.close();

				ResultSet rs3 = meta.getImportedKeys(null, null, tableName);
				while (rs3.next()) {
					String pkTable = rs3.getString("PKTABLE_NAME");
					String fkColumn = rs3.getString("FKCOLUMN_NAME");
					String foreignEntityName = nameTranslator.getEntityForTable(pkTable);
					
					PropertyDef propertyDef = propertyDefs.get(fkColumn);
					propertyDef.setForeignKey(true);
					propertyDef.setForeignEntityName(foreignEntityName);
				}
				rs3.close();

				return new TableSchema(entityName, propertyDefs.values());
			}
		};

		return jdbcTemplate.execute(callback);
	}

	private String getSafeString(ResultSet rs, String column) {
		try {
			return rs.getString(column);
		} catch (SQLException e) {
			return null;
		}
	}
}
