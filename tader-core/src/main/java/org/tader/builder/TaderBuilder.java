package org.tader.builder;

import java.sql.Types;
import java.util.Collection;

import org.tader.AutoGenerateSource;
import org.tader.AutoGenerateSourceContribution;
import org.tader.AutoGenerateSourceImpl;
import org.tader.EntityPersistence;
import org.tader.EntitySchema;
import org.tader.Tader;
import org.tader.TaderConstants;
import org.tader.TaderImpl;
import org.tader.TypeCoercer;
import org.tader.TypeCoercerContribution;
import org.tader.TypeCoercerImpl;
import org.tader.autogen.DefaultBigDecimalAutoGenerateStrategy;
import org.tader.autogen.DefaultBlobAutoGenerateStrategy;
import org.tader.autogen.DefaultDateAutoGenerateStrategy;
import org.tader.autogen.DefaultIntegerAutoGenerateStrategy;
import org.tader.autogen.DefaultStringAutoGenerateStrategy;
import org.tader.coercer.BigDecimalDoubleTypeCoercerContribution;
import org.tader.coercer.ByteArrayInputStreamTypeCoercerContribution;
import org.tader.coercer.DoubleBigDecimalTypeCoercerContribution;
import org.tader.coercer.IntegerLongTypeCoercerContribution;
import org.tader.coercer.LongIntegerTypeCoercerContribution;
import org.tader.coercer.SqlDateUtilDateTypeCoercerContribution;
import org.tader.coercer.TimestampDateTypeCoercerContribution;
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

/**
 * A convenient way of instantiating a {@link Tader} instance via inversion of control.
 */
public class TaderBuilder {
	private RegistryBuilder registryBuilder = new RegistryBuilder();
	
	/**
	 * Registers services in the org.tader package
	 * @return this TaderBuilder for further configuration
	 */
	public TaderBuilder withCoreServices() {
		ServiceBuilder<AutoGenerateSource> autoGenBuilder = new ServiceBuilder<AutoGenerateSource>() {
			@Override
			public AutoGenerateSource build(ServiceBuilderContext context) {
				Collection<AutoGenerateSourceContribution> contributions = context.getContributions(AutoGenerateSourceContribution.class);
				String prop = context.getProperty(TaderConstants.PROP_DEFAULT_AUTOGENERATE_NULLABLE);
				boolean autoGenerateNullable = "true".equalsIgnoreCase(prop);
				return new AutoGenerateSourceImpl(context.getService(EntitySchema.class), contributions, autoGenerateNullable);
			}
		};
		ServiceBuilder<TypeCoercer> typeCoerceBuilder = new ServiceBuilder<TypeCoercer>() {
			@Override
			public TypeCoercer build(ServiceBuilderContext context) {
				return new TypeCoercerImpl(context.getContributions(TypeCoercerContribution.class));
			}
		};
		withServiceInstance(Tader.class, TaderImpl.class);
		withServiceBuilder(AutoGenerateSource.class, autoGenBuilder);
		withServiceBuilder(TypeCoercer.class, typeCoerceBuilder);
		withProperty(TaderConstants.PROP_DEFAULT_AUTOGENERATE_NULLABLE, "false");
		return this;
	}
	
	/**
	 * Registers services in the org.tader.jdbc package
	 * @return this TaderBuilder for further configuration
	 */
	public TaderBuilder withCoreJdbcServices() {
		withServiceInstance(JdbcTemplate.class, JdbcTemplateImpl.class);
		withServiceInstance(NameTranslator.class, NoopNameTranslator.class);
		withServiceInstance(EntitySchema.class, JdbcEntitySchema.class);
		withServiceInstance(EntityPersistence.class, JdbcEntityPersistence.class);
		withServiceInstance(BlobTypeAnalyzer.class, BlobTypeAnalyzerImpl.class);
		withServiceInstance(SelectHandlerSource.class, SelectHandlerSourceImpl.class);
		withServiceInstance(InsertHandlerSource.class, InsertHandlerSourceImpl.class);
		return this;
	}
	
	/**
	 * Adds core configuration for the {@link TypeCoercer}
	 * @return this TaderBuilder for further configuration
	 */
	public TaderBuilder withCoreTypeCoercerContributions() {
		withContribution(TypeCoercer.class, new IntegerLongTypeCoercerContribution());
		withContribution(TypeCoercer.class, new LongIntegerTypeCoercerContribution());
		withContribution(TypeCoercer.class, new DoubleBigDecimalTypeCoercerContribution());
		withContribution(TypeCoercer.class, new BigDecimalDoubleTypeCoercerContribution());
		withContribution(TypeCoercer.class, new ByteArrayInputStreamTypeCoercerContribution());
		withContribution(TypeCoercer.class, new SqlDateUtilDateTypeCoercerContribution());
		withContribution(TypeCoercer.class, new TimestampDateTypeCoercerContribution());
		return this;
	}
	
	/**
	 * Adds core configuration for the {@link AutoGenerateSource}
	 * @return this TaderBuilder for further configuration
	 */
	public TaderBuilder withCoreAutoGenerateSourceContributions() {
		AutoGenerateSourceContribution contribution = new AutoGenerateSourceContribution() 
			.withAutoGenerateStrategy(Types.VARCHAR, new DefaultStringAutoGenerateStrategy())
			.withAutoGenerateStrategy(Types.INTEGER, new DefaultIntegerAutoGenerateStrategy())
			.withAutoGenerateStrategy(Types.DECIMAL, new DefaultBigDecimalAutoGenerateStrategy())
			.withAutoGenerateStrategy(Types.DATE, new DefaultDateAutoGenerateStrategy())
			.withAutoGenerateStrategy(Types.TIMESTAMP, new DefaultDateAutoGenerateStrategy())
			.withAutoGenerateStrategy(Types.BLOB, new DefaultBlobAutoGenerateStrategy());

		withContribution(AutoGenerateSource.class, contribution);
		return this;
	}
	
	/**
	 * Adds a service instance to the builder
	 * @param serviceInterface The service interface
	 * @param service The service instance
	 * @return this TaderBuilder for further configuration
	 */
	public <I, T extends I> TaderBuilder withService(Class<I> serviceInterface, T service) {
		registryBuilder.withService(serviceInterface, service);
		return this;
	}

	/**
	 * Convenience method to register a {@link ConnectionSource}
	 * @param connectionSource The connection source
	 * @return this TaderBuilder for further configuration
	 */
	public TaderBuilder withConnectionSource(ConnectionSource connectionSource) {
		registryBuilder.withService(ConnectionSource.class, connectionSource);
		return this;
	}

	/**
	 * Adds a service builder to the builder
	 * @param serviceInterface The service interface
	 * @param builder Callback to instantiate a service instance
	 * @return this TaderBuilder for further configuration
	 */
	public <T> TaderBuilder withServiceBuilder(Class<T> serviceInterface, ServiceBuilder<T> builder) {
		registryBuilder.withServiceBuilder(serviceInterface, builder);
		return this;
	}

	/**
	 * Registers a service builder that will reflectively invoke the constructor of serviceType, passing any
	 * services to it that match the constructor argument types
	 * @param serviceInterface The service interface
	 * @param serviceType The concrete service type
	 * @return this TaderBuilder for further configuration
	 */
	public <I, T extends I> TaderBuilder withServiceInstance(Class<I> serviceInterface, Class<T> serviceType) {
		registryBuilder.withServiceInstance(serviceInterface, serviceType);
		return this;
	}

	/**
	 * Registers a service contribution for a service. These will be aggregated together into a {@link Collection}
	 * which can be accessed by {@link ServiceBuilder}s.
	 * @param serviceInterface The service interface
	 * @param contribution A single contribution entry which will be aggregated into a collection prior to building the service
	 * @return this TaderBuilder for further configuration
	 */
	public TaderBuilder withContribution(Class<?> serviceInterface, Object contribution) {
		registryBuilder.withContribution(serviceInterface, contribution);
		return this;
	}

	/**
	 * Sets a property on the builder. These properties are accessible to {@link ServiceBuilder}s
	 * @param name The property name
	 * @param value The property value
	 * @return this TaderBuilder for further configuration
	 */
	public TaderBuilder withProperty(String name, String value) {
		registryBuilder.withProperty(name, value);
		return this;
	}

	/**
	 * Builds the service registry and returns the {@link Tader} service
	 * @return A {@link Tader} instance
	 */
	public Tader build() {
		return buildRegistry().getService(Tader.class);
	}

	protected Registry buildRegistry() {
		return registryBuilder.build();
	}
}
