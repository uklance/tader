package org.grater.model;

import org.grater.Grater;
import org.grater.ValueGenerator;
import org.grater.ValueParser;

public interface GraterModel {
	/*
    // convert dsl value to db value
    ValueParser getValueParser(Table table, Column column, Object value, String prefix);
    ValueParser getDefaultValueParser(int type, Object value, String prefix);

    // generate value not provided by user in dsl prior to insert
    ValueGenerator getValueGenerator(Table table, Column column);
    ValueGenerator getNullableValueGenerator(Table table, Column column);
    ValueGenerator getDefaultValueGenerator(int type);
    ValueGenerator getDefaultNullableValueGenerator(int type);

    // convert db value to value in dsl
    ValueReader getValueReader(Table table, Column column);
    ValueReader getDefaultValueReader(int type);
    
    Table getTable(String table);
    Column getColumn(String table, String column);
    */
	
	TableModel getTable(String table);
	ValueGenerator getValueGenerator(Grater grater, ColumnModel column);
	ValueParser getValueParser(ColumnModel column, Object value);
	int getNextIncrement(TableModel table);
}
