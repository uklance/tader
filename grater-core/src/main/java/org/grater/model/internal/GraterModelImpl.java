package org.grater.model.internal;

import java.util.Map;

import org.grater.Grater;
import org.grater.ValueGenerator;
import org.grater.ValueParser;
import org.grater.model.ColumnModel;
import org.grater.model.GraterModel;
import org.grater.model.TableModel;

public class GraterModelImpl implements GraterModel {
	private final Map<String, TableModel> tableModels;
	
	public GraterModelImpl(Map<String, TableModel> tableModels) {
		super();
		this.tableModels = tableModels;
	}

	@Override
	public TableModel getTable(String table) {
		return tableModels.get(table);
	}
	
	@Override
	public int getNextIncrement(TableModel table) {
		throw new UnsupportedOperationException("getNextIncrement");
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
