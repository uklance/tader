package org.grater;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.grater.model.Column;
import org.grater.model.ForeignKey;
import org.grater.model.PrimaryKey;
import org.grater.model.Schema;
import org.grater.model.Table;
import org.grater.model.internal.ColumnImpl;
import org.grater.model.internal.ForeignKeyImpl;
import org.grater.model.internal.PrimaryKeyImpl;
import org.grater.model.internal.SchemaImpl;
import org.grater.model.internal.TableImpl;

public class ReverseEngineerSchemaSource implements SchemaSource {
	private final ConnectionSource connectionSource;
	private String catalog;
	private String schema;
	
	public ReverseEngineerSchemaSource(ConnectionSource connectionSource, String catalog, String schema) {
		super();
		this.connectionSource = connectionSource;
		this.catalog = catalog;
		this.schema = schema;
	}

	public ReverseEngineerSchemaSource(ConnectionSource connectionSource) {
		super();
		this.connectionSource = connectionSource;
	}

	@Override
	public Schema getSchema() {
		Connection con = connectionSource.getConnection();
		try {
			catalog = con.getCatalog();
			List<Table> tables = new ArrayList<Table>();
			DatabaseMetaData metaData = con.getMetaData();
			ResultSet rs = metaData.getTables(catalog, schema, null, null);
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				
				List<Column> columns = getColumns(metaData, tableName);
				PrimaryKey primaryKey = getPrimaryKey(metaData, tableName);
				List<ForeignKey> foreignKeys = getForeignKeys(metaData, tableName);
				TableImpl table = new TableImpl(tableName, columns, primaryKey, foreignKeys);
				tables.add(table);
			}
			return new SchemaImpl(tables);
		} catch (SQLException e) {
			throw new GraterException(e);
		} finally {
			connectionSource.returnConnection(con);
		}
	}


	protected List<ForeignKey> getForeignKeys(DatabaseMetaData metaData, String tableName) throws SQLException {
		List<ForeignKey> foreignKeys = new ArrayList<ForeignKey>();
		ResultSet rs = metaData.getImportedKeys(catalog, schema, tableName);
		while (rs.next()) {
			String column = rs.getString("FKCOLUMN_NAME");
			String referenceTable = rs.getString("PKTABLE_NAME");
			String referenceColumn = rs.getString("PKCOLUMN_NAME");
			
			foreignKeys.add(new ForeignKeyImpl(column, referenceTable, referenceColumn));
		}
		rs.close();
		return foreignKeys;
	}

	protected PrimaryKey getPrimaryKey(DatabaseMetaData metaData, String tableName) throws SQLException {
		List<String> columns = new ArrayList<String>();
		ResultSet rs = metaData.getPrimaryKeys(catalog, schema, tableName);
		while (rs.next()) {
			columns.add(rs.getString("COLUMN_NAME"));
		}
		rs.close();
		return columns.isEmpty() ? null : new PrimaryKeyImpl(columns);
	}

	protected List<Column> getColumns(DatabaseMetaData metaData, String tableName) throws SQLException {
		List<Column> columns = new ArrayList<Column>();
		ResultSet rs = metaData.getColumns(catalog, schema, tableName, null);
		while (rs.next()) {
			String columnName = rs.getString("COLUMN_NAME");
			int type = rs.getInt("DATA_TYPE");
			boolean nullable = rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable;
			int size = rs.getInt("COLUMN_SIZE");
			Column column = new ColumnImpl(columnName, type, nullable, size);
			columns.add(column);
		}
		rs.close();
		return columns;
	}
}
