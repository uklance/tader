package org.grater.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.grater.ColumnNameResolver;
import org.grater.GraterModelSource;
import org.grater.TableNameResolver;
import org.grater.TableOrderer;
import org.grater.model.Column;
import org.grater.model.ColumnModel;
import org.grater.model.GraterModel;
import org.grater.model.Schema;
import org.grater.model.Table;
import org.grater.model.TableModel;
import org.grater.model.internal.ColumnModelImpl;
import org.grater.model.internal.GraterModelImpl;
import org.grater.model.internal.TableModelImpl;

public class GraterModelSourceImpl implements GraterModelSource {
	private final TableOrderer tableOrderer;
	private final TableNameResolver tableNameResolver;
	private final ColumnNameResolver columnNameResolver;
	
	public GraterModelSourceImpl(TableOrderer tableOrderer, TableNameResolver tableNameResolver,
			ColumnNameResolver columnNameResolver) {
		super();
		this.tableOrderer = tableOrderer;
		this.tableNameResolver = tableNameResolver;
		this.columnNameResolver = columnNameResolver;
	}

	public GraterModel getGraterModel(Schema schema) {
		Map<String, TableModel> tableModels = new LinkedHashMap<String, TableModel>();
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
					
			Set<ColumnModel> pk = new LinkedHashSet<ColumnModel>();
			
			for (Column column : table.getColumns()) {
				String columnName = columnNameResolver.resolveName(column);
				
				ColumnModel columnModel = new ColumnModelImpl(tableModel, column, columnName);
				tableModel.addColumn(columnModel);
				
				if (unresolvedPk.contains(column.getName())) {
					pk.add(columnModel);
				}
			}
			
			if (unresolvedPk.size() != pk.size()) {
				throw new IllegalStateException(String.format("Bad primary key %s %s", unresolvedPk, pk));
			}
			
			tableModel.setPrimaryKey(pk);
			tableModels.put(tableName, tableModel);
		}
		
		return new GraterModelImpl(tableModels);
	}
}
