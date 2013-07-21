package org.grater.model;

import java.util.Collection;
import java.util.Set;

public interface TableModel {
	ColumnModel getForeignColumn(String name);
	boolean isForeignKey(String name);
	boolean isPrimaryKey(String name);
	ColumnModel getColumn(String name);
	Collection<ColumnModel> getColumns();
	int getOrder();
	String getName();
	Table getTable();
	Set<ColumnModel> getPrimaryKey();
}
