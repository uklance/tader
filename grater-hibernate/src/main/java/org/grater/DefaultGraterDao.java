package org.grater;

import java.sql.Connection;
import java.util.Map;

import org.grater.GraterDao;
import org.grater.model.TableModel;
import org.hibernate.dialect.Dialect;

public class DefaultGraterDao implements GraterDao {
	private final Dialect dialect;
	
	public DefaultGraterDao(Dialect dialect) {
		super();
		this.dialect = dialect;
	}

	@Override
	public Map<String, Object> insert(Connection con, TableModel table, Map<String, Object> values) {
		throw new UnsupportedOperationException("insert");
	}

	@Override
	public Map<String, Object> select(Connection con, TableModel table, Map<String, Object> pk) {
		throw new UnsupportedOperationException("select");
	}

	@Override
	public Map<String, Object> delete(Connection con, TableModel table, Map<String, Object> pk) {
		throw new UnsupportedOperationException("delete");
	}
	
	/*
	protected Entity doSelect(Connection con, TableModel table, Map<String, Object> primaryKey) {
		StringBuilder sql = new StringBuilder("select * from " + table.getTable().getName() + " where");
		Object[] pkValues = new Object[table.getPrimaryKey().size()];
		int[] pkTypes = new int[table.getPrimaryKey().size()];
		int i = 0;
		for (ColumnModel pkColumn : table.getPrimaryKey()) {
			Object pkValue = primaryKey.get(pkColumn.getName());
			if (pkValue == null) {
				throw new GraterException(String.format("Primary key column %s not provided", pkColumn));
			}
			pkValues[i] = parseValue(pkColumn, pkValue);
			pkTypes[i] = pkColumn.getColumn().getType();
			
			if (i != 0) {
				sql.append(" and");
			}
			sql.append(" " + pkColumn.getColumn().getName() + " = ?");
			++i;
		}
		PreparedStatement ps = con.prepareStatement(sql.toString());
	}

	protected void doInsert(Connection con, TableModel table, Map<String, Object> values) {
	}
	*/
}
