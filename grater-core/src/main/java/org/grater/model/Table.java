package org.grater.model;

import java.util.Collection;

public interface Table {
	String getName();
	Collection<Column> getColumns();
	PrimaryKey getPrimaryKey();
	Collection<ForeignKey> getForeignKeys();
}
