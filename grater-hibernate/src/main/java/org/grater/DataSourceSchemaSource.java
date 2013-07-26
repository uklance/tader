package org.grater;

import javax.sql.DataSource;

import org.grater.SchemaSource;
import org.grater.model.Schema;
import org.hibernate.dialect.Dialect;

public class DataSourceSchemaSource implements SchemaSource {
	private final DataSource dataSource;
	private final Dialect dialect;
	
	public DataSourceSchemaSource(DataSource dataSource, Dialect dialect) {
		super();
		this.dataSource = dataSource;
		this.dialect = dialect;
	}

	@Override
	public Schema getSchema() {
		throw new UnsupportedOperationException("getSchema");
	}
}
