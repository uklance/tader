package org.tader.builder;

import java.sql.Types;
import java.util.Collection;

import org.tader.AutoGenerateSource;
import org.tader.AutoGenerateSourceContribution;
import org.tader.AutoGenerateSourceImpl;
import org.tader.ByteArrayInputStreamTypeCoercerContribution;
import org.tader.DefaultStringAutoGenerateStrategy;
import org.tader.EntityPersistence;
import org.tader.EntitySchema;
import org.tader.IntegerLongTypeCoercerContribution;
import org.tader.Tader;
import org.tader.TaderImpl;
import org.tader.TypeCoercer;
import org.tader.jdbc.BlobTypeAnalyzer;
import org.tader.jdbc.BlobTypeAnalyzerImpl;
import org.tader.jdbc.ConnectionSource;
import org.tader.jdbc.InsertHandlerSource;
import org.tader.jdbc.InsertHandlerSourceImpl;
import org.tader.jdbc.JdbcEntityPersistence;
import org.tader.jdbc.JdbcEntitySchema;
import org.tader.jdbc.JdbcTemplate;
import org.tader.jdbc.JdbcTemplateImpl;
import org.tader.jdbc.NameTranslator;
import org.tader.jdbc.NoopNameTranslator;
import org.tader.jdbc.SelectHandlerSource;
import org.tader.jdbc.SelectHandlerSourceImpl;
import org.tader.jdbc.TypeCoercerContribution;
import org.tader.jdbc.TypeCoercerImpl;

public class TaderBuilder {
	private RegistryBuilder registryBuilder = new RegistryBuilder();
	
	public TaderBuilder withCoreServices() {
		ServiceBuilder<AutoGenerateSource> autoGenBuilder = new ServiceBuilder<AutoGenerateSource>() {
			@Override
			public AutoGenerateSource build(ServiceBuilderContext context) {
				Collection<AutoGenerateSourceContribution> contributions = context.getContributions(AutoGenerateSource.class, AutoGenerateSourceContribution.class);
				return new AutoGenerateSourceImpl(context.getService(EntitySchema.class), contributions);
			}
		};
		ServiceBuilder<TypeCoercer> typeCoerceBuilder = new ServiceBuilder<TypeCoercer>() {
			@Override
			public TypeCoercer build(ServiceBuilderContext context) {
				return new TypeCoercerImpl(context.getContributions(TypeCoercer.class, TypeCoercerContribution.class));
			}
		};
		registryBuilder.withServiceInstance(Tader.class, TaderImpl.class);
		registryBuilder.withServiceBuilder(AutoGenerateSource.class, autoGenBuilder);
		registryBuilder.withServiceBuilder(TypeCoercer.class, typeCoerceBuilder);
		return this;
	}
	
	public TaderBuilder withCoreJdbcServices() {
		registryBuilder.withServiceInstance(JdbcTemplate.class, JdbcTemplateImpl.class);
		registryBuilder.withServiceInstance(NameTranslator.class, NoopNameTranslator.class);
		registryBuilder.withServiceInstance(EntitySchema.class, JdbcEntitySchema.class);
		registryBuilder.withServiceInstance(EntityPersistence.class, JdbcEntityPersistence.class);
		registryBuilder.withServiceInstance(BlobTypeAnalyzer.class, BlobTypeAnalyzerImpl.class);
		registryBuilder.withServiceInstance(SelectHandlerSource.class, SelectHandlerSourceImpl.class);
		registryBuilder.withServiceInstance(InsertHandlerSource.class, InsertHandlerSourceImpl.class);
		return this;
	}
	
	public TaderBuilder withCoreTypeCoercerContributions() {
		registryBuilder.withContribution(TypeCoercer.class, new IntegerLongTypeCoercerContribution());
		registryBuilder.withContribution(TypeCoercer.class, new ByteArrayInputStreamTypeCoercerContribution());
		return this;
	}
	
	public TaderBuilder withCoreAutoGenerateSourceContributions() {
		AutoGenerateSourceContribution contribution = new AutoGenerateSourceContribution() 
			.withAutoGenerateStrategy(Types.VARCHAR, new DefaultStringAutoGenerateStrategy());

		registryBuilder.withContribution(AutoGenerateSource.class, contribution);
		return this;
	}
	
	public <I, T extends I> TaderBuilder withService(Class<I> serviceInterface, T service) {
		registryBuilder.withService(serviceInterface, service);
		return this;
	}

	public TaderBuilder withConnectionSource(ConnectionSource connectionSource) {
		registryBuilder.withService(ConnectionSource.class, connectionSource);
		return this;
	}

	public <T> TaderBuilder withServiceBuilder(Class<T> serviceInterface, ServiceBuilder<T> builder) {
		registryBuilder.withServiceBuilder(serviceInterface, builder);
		return this;
	}

	public <I, T extends I> TaderBuilder withServiceInstance(Class<I> serviceInterface, Class<T> serviceType) {
		registryBuilder.withServiceInstance(serviceInterface, serviceType);
		return this;
	}

	public TaderBuilder withContribution(Class<?> serviceInterface, Object contribution) {
		registryBuilder.withContribution(serviceInterface, contribution);
		return this;
	}

	public TaderBuilder withProperty(String name, String value) {
		registryBuilder.withProperty(name, value);
		return this;
	}
	
	public Tader build() {
		return registryBuilder.build().getService(Tader.class);
	}
}
