package org.grater.internal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.grater.Grater;
import org.grater.GraterException;
import org.grater.IncrementProvider;
import org.grater.TableRow;
import org.grater.ValueGenerator;
import org.grater.ValueParser;
import org.grater.model.ColumnModel;
import org.grater.model.GraterModel;
import org.grater.model.TableModel;

public class GraterImpl implements Grater {
	private final GraterModel model;
	private final DataSource dataSource;
	
	public GraterImpl(GraterModel model, DataSource dataSource) {
		super();
		this.model = model;
		this.dataSource = dataSource;
	}

	@Override
	public TableRow insert(String table, Object... columnsAndValues) {
		return insert(table, GraterUtils.asMap(String.class, Object.class, columnsAndValues));
	}
	
	@Override
	public TableRow insert(String table, Map<String, Object> values) {
		return insert(new TableRowImpl(table, values));
	}
	
	@Override
	public TableRow insert(TableRow row) {
		final TableModel table = model.getTable(row.getTable());
		Map<String, Object> preValues = row.getValues();
		
		Map<String, Object> postValues = new LinkedHashMap<String, Object>();
		
		IncrementProvider incrementProvider = new IncrementProvider() {
			private Integer increment;

			public int getIncrement() {
				if (increment == null) {
					increment = model.getNextIncrement(table);
				}
				return increment;
			}
		};
		try {
			Connection con = dataSource.getConnection();
			
			try {
				for (ColumnModel column : table.getColumns()) {
					Object postValue;
					boolean isForeign = table.isForeignKey(column.getName());
					if (preValues.containsKey(column.getName())) {
						Object preValue = preValues.get(column.getName());
						if (isForeign) {
							postValue = parseForeignValue(column, preValue);
						} else {
							postValue = parseValue(column, preValue);
						}
					} else {
						if (isForeign) {
							postValue = generateForeignValue(column);
						} else {
							postValue = generateValue(column, incrementProvider);
						}
					}
					postValues.put(column.getName(), postValue);
				}
				
				doInsert(con, table, postValues);
				return new TableRowImpl(table.getName(), postValues);
			} finally {
				con.close();
			}
		} catch (SQLException e) {
			throw new GraterException(e);
		}
	}

	protected Object generateValue(ColumnModel column, IncrementProvider incrementProvider) {
		ValueGenerator generator = model.getValueGenerator(this, column);
		return generator.generateValue(column.getTable().getTable(), column.getColumn(), incrementProvider);
	}

	private Object generateForeignValue(ColumnModel column) {
		throw new UnsupportedOperationException("generateForeignValue");
	}

	protected Object parseValue(ColumnModel column, Object preValue) {
		ValueParser parser = model.getValueParser(column, preValue);
		return parser.parseValue(column.getTable().getTable(), column.getColumn(), preValue);
	}

	protected TableRow parseForeignValue(ColumnModel column, Object preValue) {
		Map<String, Object> values = asMap(preValue);
		
		TableModel table = column.getTable();
		ColumnModel foreignColumn = table.getForeignColumn(column.getName());
		TableModel foreignTable = foreignColumn.getTable();
		
		Set<ColumnModel> foreignPk = foreignTable.getPrimaryKey();
		if (foreignPk.size() == 1) {
			throw new GraterException(String.format(
				"table %s has %s primary key columns, expected 1. Referenced by %s.%s",
				foreignTable.getName(), foreignPk.size(), table.getName(), column.getName()
			));
		}
		ColumnModel foreignPkColumn = foreignPk.iterator().next();

		boolean isForeignPkProvided = values.get(foreignPkColumn.getName()) != null;
		
		if (isForeignPkProvided) {
			return select(foreignTable.getName(), values);
		}

		return insert(foreignTable.getName(), values);
	}
	
	protected TableRow doSelect(Connection con, TableModel table, Map<String, Object> primaryKey) {
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
	
	protected Map<String, Object> asMap(Object preValue) {
		if (preValue == null) {
			return Collections.emptyMap();
		}
		if (preValue instanceof Map) {
			return (Map) preValue;
		}
		throw new GraterException("Illegal type " + preValue.getClass().getName());
	}

	protected void doInsert(Connection con, TableModel table, Map<String, Object> values) {
	}
}
