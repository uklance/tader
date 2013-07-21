package org.grater.model.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.grater.ColumnNameResolver;
import org.grater.Grater;
import org.grater.TableNameResolver;
import org.grater.TableOrderer;
import org.grater.ValueGenerator;
import org.grater.ValueParser;
import org.grater.model.Column;
import org.grater.model.ColumnModel;
import org.grater.model.GraterModel;
import org.grater.model.Schema;
import org.grater.model.Table;
import org.grater.model.TableModel;

public class GraterModelImpl implements GraterModel {
	private Map<String, TableModel> tableModels = new LinkedHashMap<String, TableModel>();

	public GraterModelImpl(Schema schema, TableOrderer tableOrderer, TableNameResolver tableNameResolver, ColumnNameResolver columnNameResolver) {
		Collection<Table> tables = schema.getTables();
		List<Table> tableOrder = tableOrderer.getTableOrder(tables);
		if (tables.size() != tableOrder.size()) {
			throw new RuntimeException(String.format("Expected %s tables from TableOrderer found %s", tables.size(), tableOrder.size()));
		}
		int i = 0;
		for (Table table : tableOrder) {
			String tableName = tableNameResolver.resolveName(table);
			TableModelImpl tableModel = new TableModelImpl(tableName, table, i++);

			Set<String> unresolvedPk = table.getPrimaryKey() == null ? 
					Collections.<String>emptySet() : new HashSet<String>(table.getPrimaryKey().getColumns());
					
			Set<String> pk = new LinkedHashSet<String>();
			
			for (Column column : table.getColumns()) {
				String columnName = columnNameResolver.resolveName(column);
				
				ColumnModel columnModel = new ColumnModelImpl(tableModel, column, columnName);
				tableModel.addColumn(columnModel);
				
				if (unresolvedPk.contains(column.getName())) {
					pk.add(columnName);
				}
			}
			
			if (unresolvedPk.size() != pk.size()) {
				throw new IllegalStateException(String.format("Bad primary key %s %s", unresolvedPk, pk));
			}
			
			tableModel.setPrimaryKey(pk);
			tableModels.put(tableName, tableModel);
		}
	}
	
	@Override
	public TableModel getTable(String table) {
		return tableModels.get(table);
	}
	
	@Override
	public ValueGenerator getValueGenerator(Grater grater, ColumnModel column) {
		throw new UnsupportedOperationException("getValueGenerator");
	}
	
	@Override
	public ValueParser getValueParser(ColumnModel column, Object value) {
		throw new UnsupportedOperationException("getValueParser");
	}
}
