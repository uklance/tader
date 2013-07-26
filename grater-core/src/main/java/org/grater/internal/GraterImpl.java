package org.grater.internal;

import java.sql.Connection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.grater.ConnectionSource;
import org.grater.Entity;
import org.grater.Grater;
import org.grater.GraterDao;
import org.grater.GraterException;
import org.grater.IncrementProvider;
import org.grater.PartialEntity;
import org.grater.ValueGenerator;
import org.grater.ValueParser;
import org.grater.model.ColumnModel;
import org.grater.model.GraterModel;
import org.grater.model.TableModel;

public class GraterImpl implements Grater {
	private final GraterModel model;
	private final GraterDao dao;
	private final ConnectionSource connectionSource;
	
	public GraterImpl(GraterModel model, ConnectionSource connectionSource, GraterDao dao) {
		super();
		this.model = model;
		this.connectionSource = connectionSource;
		this.dao = dao;
	}

	@Override
	public Entity insert(String table, Object... columnsAndValues) {
		return insert(table, GraterUtils.asMap(String.class, Object.class, columnsAndValues));
	}
	
	@Override
	public Entity insert(String table, Map<String, Object> values) {
		return insert(new PartialEntityImpl(table, values));
	}
	
	@Override
	public Entity insert(PartialEntity partial) {
		final TableModel table = model.getTable(partial.getTable());
		Map<String, Object> partialValues = partial.getValues();		
		Map<String, Object> preInsertValues = new LinkedHashMap<String, Object>();
		
		IncrementProvider incrementProvider = new IncrementProvider() {
			private Integer increment;

			public int getIncrement() {
				if (increment == null) {
					increment = model.getNextIncrement(table);
				}
				return increment;
			}
		};
		Connection con = connectionSource.getConnection();
		
		try {
			for (ColumnModel column : table.getColumns()) {
				Object value;
				boolean isForeign = table.isForeignKey(column.getName());
				if (partialValues.containsKey(column.getName())) {
					Object preValue = partialValues.get(column.getName());
					if (isForeign) {
						value = parseForeignValue(column, preValue);
					} else {
						value = parseValue(column, preValue);
					}
				} else {
					if (isForeign) {
						ColumnModel foreignColumn = table.getForeignColumn(column.getName());
						value = generateForeignValue(con, foreignColumn);
					} else {
						value = generateValue(column, incrementProvider);
					}
				}
				preInsertValues.put(column.getName(), value);
			}
			
			Map<String, Object> postInsertValues = dao.insert(con, table, preInsertValues);
			return new EntityImpl(table.getName(), postInsertValues);
		} finally {
			connectionSource.returnConnection(con);
		}
	}
	
	@Override
	public Entity select(String table, Map<String, Object> pk) {
		Connection con = connectionSource.getConnection();
		try {
			Map<String, Object> values = dao.select(con, model.getTable(table), pk);
			return new EntityImpl(table, values);
		} finally {
			connectionSource.returnConnection(con);
		}
	}
	
	@Override
	public Entity select(String table, Object pk) {
		TableModel tableModel = model.getTable(table);
		Set<ColumnModel> primaryKey = tableModel.getPrimaryKey();
		if (primaryKey.size() == 1) {
			throw new GraterException(String.format("%s has %s primary key columns, expected 1", table, primaryKey.size()));
		}
		String pkColumnName = primaryKey.iterator().next().getName();
		Map<String, Object> values = GraterUtils.asMap(String.class, Object.class, pkColumnName, pk);
		return select(table, values);
	}

	protected Object generateValue(ColumnModel column, IncrementProvider incrementProvider) {
		ValueGenerator generator = model.getValueGenerator(this, column);
		return generator.generateValue(column.getTable().getTable(), column.getColumn(), incrementProvider);
	}

	protected Entity generateForeignValue(Connection con, ColumnModel foreignColumn) {
		return insert(foreignColumn.getTable().getName(), Collections.emptyMap());
	}

	protected Object parseValue(ColumnModel column, Object preValue) {
		ValueParser parser = model.getValueParser(column, preValue);
		return parser.parseValue(column.getTable().getTable(), column.getColumn(), preValue);
	}

	protected Entity parseForeignValue(ColumnModel column, Object preValue) {
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
	
	protected Map<String, Object> asMap(Object value) {
		if (value == null) {
			return Collections.emptyMap();
		}
		if (value instanceof Map) {
			return (Map) value;
		}
		if (value instanceof PartialEntity) {
			return ((PartialEntity) value).getValues();
		}
		throw new GraterException("Illegal type " + value.getClass().getName());
	}
}
