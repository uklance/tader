package org.grater.model.internal;

import java.util.Collection;

import org.grater.internal.GraterUtils;
import org.grater.model.Schema;
import org.grater.model.Table;

public class SchemaImpl implements Schema {
	private final Collection<Table> tables;

	public SchemaImpl(Collection<Table> tables) {
		super();
		this.tables = GraterUtils.asImmutableCollection(tables);
	}
	
	public Collection<Table> getTables() {
		return tables;
	}
}
