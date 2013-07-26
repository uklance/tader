package org.grater.hibernate;

import org.grater.SchemaSource;
import org.grater.model.Schema;

public class DataSourceSchemaSource implements SchemaSource {
	@Override
	public Schema getSchema() {
		throw new UnsupportedOperationException("getSchema");
	}
}
