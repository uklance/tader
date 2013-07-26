package org.grater;

import javax.sql.DataSource;

import org.grater.internal.GraterImpl;
import org.grater.internal.GraterModelSourceImpl;
import org.grater.internal.TableOrdererImpl;
import org.grater.model.GraterModel;
import org.hibernate.dialect.Dialect;

public class GraterBuilder {
	private SchemaSource schemaSource;
	private TableOrderer tableOrderer = new TableOrdererImpl();
	private TableNameResolver tableNameResolver = new CamelCaseTableNameResolver();
	private ColumnNameResolver columnNameResolver = new CamelCaseColumnNameResolver();
	private ConnectionSource connectionSource;
	private Dialect dialect;
	
	public GraterBuilder withSchemaSource(SchemaSource schemaSource) {
		this.schemaSource = schemaSource;
		return this;
	}
	
	public GraterBuilder withTableOrderer(TableOrderer tableOrderer) {
		this.tableOrderer = tableOrderer;
		return this;
	}
	
	public GraterBuilder withTableNameResolver(TableNameResolver tableNameResolver) {
		this.tableNameResolver = tableNameResolver;
		return this;
	}
	
	public GraterBuilder withColumnNameResolver(ColumnNameResolver columnNameResolver) {
		this.columnNameResolver = columnNameResolver;
		return this;
	}
	
	public GraterBuilder withDialect(Dialect dialect) {
		this.dialect = dialect;
		return this;
	}
	
	public GraterBuilder withDataSource(DataSource ds) {
		return withConnectionSource(new DataSourceConnectionSource(ds));
	}

	public GraterBuilder withConnectionSource(ConnectionSource connectionSource) {
		this.connectionSource = connectionSource;
		return this;
	}
	
	public Grater build() {
		assertDependency(tableOrderer, "tableOrderer");
		assertDependency(tableNameResolver, "tableNameResolver");
		assertDependency(columnNameResolver, "columnNameResolver");
		assertDependency(schemaSource, "schemaSource");
		assertDependency(connectionSource, "connectionSource");
		assertDependency(dialect, "dialect");
		GraterModelSource modelSource = new GraterModelSourceImpl(tableOrderer, tableNameResolver, columnNameResolver);
		GraterModel model = modelSource.getGraterModel(schemaSource.getSchema());
		GraterDao dao = new DefaultGraterDao(dialect);
		return new GraterImpl(model, connectionSource, dao);
	}
	
	protected void assertDependency(Object dependency, String name) {
		if (dependency == null) {
			throw new GraterException(String.format("Required dependency %s not provided", name));
		}
	}
}
