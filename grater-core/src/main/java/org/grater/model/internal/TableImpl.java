package org.grater.model.internal;

import java.util.Collection;

import org.grater.internal.GraterUtils;
import org.grater.model.Column;
import org.grater.model.ForeignKey;
import org.grater.model.PrimaryKey;
import org.grater.model.Table;

public class TableImpl implements Table {
	private final String name;
	private final Collection<Column> columns;
	private final PrimaryKey primaryKey;
	private final Collection<ForeignKey> foreignKeys;
	
	public TableImpl(String name, Collection<Column> columns, PrimaryKey primaryKey, Collection<ForeignKey> foreignKeys) {
		super();
		this.name = name;
		this.columns = GraterUtils.asImmutableCollection(columns);
		this.primaryKey = primaryKey;
		this.foreignKeys = GraterUtils.asImmutableCollection(foreignKeys);
	}

	public String getName() {
		return name;
	}

	public Collection<Column> getColumns() {
		return columns;
	}

	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}

	public Collection<ForeignKey> getForeignKeys() {
		return foreignKeys;
	}
	
	@Override
	public String toString() {
		return String.format("Table[%s]", name);
	}
}
