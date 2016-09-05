package org.tader.builder;

import java.sql.Types;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

import org.tader.AutoGenerateSource;
import org.tader.AutoGenerateSourceContribution;
import org.tader.AutoGenerateSourceImpl;
import org.tader.AutoGenerateStrategy;
import org.tader.EntityPersistence;
import org.tader.EntitySchema;
import org.tader.PropertySource;
import org.tader.PropertySourceImpl;
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
import org.tader.autogen.QueryAutoGenerateStrategy;
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

import com.lazan.tinyioc.ServiceBinder;
import com.lazan.tinyioc.ServiceBuilder;
import com.lazan.tinyioc.ServiceBuilderContext;
import com.lazan.tinyioc.ServiceModule;
import com.lazan.tinyioc.ServiceRegistry;
import com.lazan.tinyioc.ServiceRegistryBuilder;
import com.lazan.tinyioc.annotations.Bind;
import com.lazan.tinyioc.internal.ConstantServiceBuilder;

/**
 * A convenient way of instantiating a {@link Tader} instance via inversion of control.
 */
public class TaderBuilder {
	private ServiceRegistryBuilder registryBuilder = new ServiceRegistryBuilder();
	private AtomicLong nextContributionId = new AtomicLong(1L);
	
	public static class CoreServicesModule {
		@Bind
		public void bind(ServiceBinder binder) {
			ServiceBuilder<AutoGenerateSource> autoGenBuilder = new ServiceBuilder<AutoGenerateSource>() {
				@Override
				public AutoGenerateSource build(ServiceBuilderContext context) {
					PropertySource propertySource = context.getServiceRegistry().getService(PropertySource.class);
					EntitySchema entitySchema = context.getServiceRegistry().getService(EntitySchema.class);
					Collection<AutoGenerateSourceContribution> contributions = context.getUnorderedContributions(AutoGenerateSourceContribution.class);
					String prop = propertySource.getProperty(TaderConstants.PROP_DEFAULT_AUTOGENERATE_NULLABLE);
					boolean autoGenerateNullable = "true".equalsIgnoreCase(prop);
					return new AutoGenerateSourceImpl(entitySchema, contributions, autoGenerateNullable);
				}
			};
			ServiceBuilder<TypeCoercer> typeCoerceBuilder = new ServiceBuilder<TypeCoercer>() {
				@SuppressWarnings("rawtypes")
				@Override
				public TypeCoercer build(ServiceBuilderContext context) {
					Collection<TypeCoercerContribution> contributions = context.getUnorderedContributions(TypeCoercerContribution.class);
					return new TypeCoercerImpl(contributions);
				}
			};
			binder.bind(Tader.class, TaderImpl.class);
			binder.bind(AutoGenerateSource.class, autoGenBuilder).withUnorderedContribution(AutoGenerateSourceContribution.class);
			binder.bind(TypeCoercer.class, typeCoerceBuilder).withUnorderedContribution(TypeCoercerContribution.class);
			binder.bind(PropertySource.class, new ServiceBuilder<PropertySource>() {
				@Override
				public PropertySource build(ServiceBuilderContext context) {
					return new PropertySourceImpl(context.getMappedContributions(String.class, String.class));
				}
			}).withMappedContribution(String.class, String.class);
			binder.mappedContribution(PropertySource.class, TaderConstants.PROP_DEFAULT_AUTOGENERATE_NULLABLE, TaderConstants.PROP_DEFAULT_AUTOGENERATE_NULLABLE, "false");
		}
	}
	
	/**
	 * Registers services in the org.tader package
	 * @return this TaderBuilder for further configuration
	 */
	public TaderBuilder withCoreServices() {
		registryBuilder.withModuleType(CoreServicesModule.class);
		return this;
	}
	
	/**
	 * Registers services in the org.tader.jdbc package
	 * @return this TaderBuilder for further configuration
	 */
	public TaderBuilder withCoreJdbcServices() {
		withService(JdbcTemplate.class, JdbcTemplateImpl.class);
		withService(NameTranslator.class, NoopNameTranslator.class);
		withService(EntitySchema.class, JdbcEntitySchema.class);
		withService(EntityPersistence.class, JdbcEntityPersistence.class);
		withService(BlobTypeAnalyzer.class, BlobTypeAnalyzerImpl.class);
		withService(SelectHandlerSource.class, SelectHandlerSourceImpl.class);
		withService(InsertHandlerSource.class, InsertHandlerSourceImpl.class);
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

		return withContribution(AutoGenerateSource.class, contribution);
	}
	
	public TaderBuilder withAutoGenerateStrategy(int sqlType, AutoGenerateStrategy strategy) {
		AutoGenerateSourceContribution contribution = new AutoGenerateSourceContribution()
			.withAutoGenerateStrategy(sqlType, strategy);
		
		return withContribution(AutoGenerateSource.class, contribution);
	}

	public TaderBuilder withAutoGenerateStrategy(String entity, String property, AutoGenerateStrategy strategy) {
		AutoGenerateSourceContribution contribution = new AutoGenerateSourceContribution()
			.withAutoGenerateStrategy(entity, property, strategy);
		
		return withContribution(AutoGenerateSource.class, contribution);
	}
	
	/**
	 * Adds a service instance to the builder
	 * @param serviceInterface The service interface
	 * @param service The service instance
	 * @return this TaderBuilder for further configuration
	 */
	public <I, T extends I> TaderBuilder withService(Class<I> serviceInterface, T service) {
		registryBuilder.withModule(new ServiceModule() {
			@Override
			public void bind(ServiceBinder binder) {
				binder.bind(serviceInterface, service);
			}
		});
		return this;
	}

	/**
	 * Adds a service builder to the builder
	 * @param serviceInterface The service interface
	 * @param builder Callback to instantiate a service instance
	 * @return this TaderBuilder for further configuration
	 */
	public <T> TaderBuilder withService(Class<T> serviceInterface, ServiceBuilder<T> builder) {
		registryBuilder.withModule(new ServiceModule() {
			@Override
			public void bind(ServiceBinder binder) {
				binder.bind(serviceInterface, builder);
			}
		});
		return this;
	}

	/**
	 * Registers a service builder that will reflectively invoke the constructor of serviceType, passing any
	 * services to it that match the constructor argument types
	 * @param serviceInterface The service interface
	 * @param serviceType The concrete service type
	 * @return this TaderBuilder for further configuration
	 */
	public <I, T extends I> TaderBuilder withService(Class<I> serviceInterface, Class<T> serviceType) {
		registryBuilder.withModule(new ServiceModule() {
			@Override
			public void bind(ServiceBinder binder) {
				binder.bind(serviceInterface, serviceType);
			}
		});
		return this;
	}

	/**
	 * Overrides a service
	 * @param serviceInterface The service interface
	 * @param service The service instance
	 * @return this TaderBuilder for further configuration
	 */
	public <I, T extends I> TaderBuilder withServiceOverride(Class<I> serviceInterface, T service) {
		registryBuilder.withModule(new ServiceModule() {
			@Override
			public void bind(ServiceBinder binder) {
				binder.override(serviceInterface, service);
			}
		});
		return this;
	}

	/**
	 * Overrides a service
	 * @param serviceInterface The service interface
	 * @param builder Callback to instantiate a service instance
	 * @return this TaderBuilder for further configuration
	 */
	public <T> TaderBuilder withServiceOverride(Class<T> serviceInterface, ServiceBuilder<T> builder) {
		registryBuilder.withModule(new ServiceModule() {
			@Override
			public void bind(ServiceBinder binder) {
				binder.override(serviceInterface, builder);
			}
		});
		return this;
	}

	/**
	 * Overrides a service using a builder that will reflectively invoke the constructor of serviceType, passing any
	 * services to it that match the constructor argument types
	 * @param serviceInterface The service interface
	 * @param serviceType The concrete service type
	 * @return this TaderBuilder for further configuration
	 */
	public <I, T extends I> TaderBuilder withServiceOverride(Class<I> serviceInterface, Class<T> serviceType) {
		registryBuilder.withModule(new ServiceModule() {
			@Override
			public void bind(ServiceBinder binder) {
				binder.override(serviceInterface, serviceType);
			}
		});
		return this;
	}
	
	/**
	 * Convenience method to register a {@link ConnectionSource}
	 * @param connectionSource The connection source
	 * @return this TaderBuilder for further configuration
	 */
	public TaderBuilder withConnectionSource(ConnectionSource connectionSource) {
		return withService(ConnectionSource.class, connectionSource);
	}

	/**
	 * Registers a service contribution for a service. These will be aggregated together into a {@link Collection}
	 * which can be accessed by {@link ServiceBuilder}s.
	 * @param serviceInterface The service interface
	 * @param contribution A single contribution entry which will be aggregated into a collection prior to building the service
	 * @return this TaderBuilder for further configuration
	 */
	public TaderBuilder withContribution(Class<?> serviceInterface, ServiceBuilder<?> builder) {
		registryBuilder.withModule(new ServiceModule() {
			@Override
			public void bind(ServiceBinder binder) {
				binder.unorderedContribution(serviceInterface, String.valueOf(nextContributionId.getAndIncrement()), builder);
			}
		});
		return this;
	}
	
	public TaderBuilder withContribution(Class<?> serviceInterface, Object contribution) {
		return withContribution(serviceInterface, new ConstantServiceBuilder<>(contribution));
	}
	
	public TaderBuilder withQueryAutoGenerateStrategy(String entityName, String propertyName, String sql, Class<?> type) {
		ServiceBuilder<AutoGenerateSourceContribution> builder = new ServiceBuilder<AutoGenerateSourceContribution>() {
			@Override
			public AutoGenerateSourceContribution build(ServiceBuilderContext context) {
				ServiceRegistry registry = context.getServiceRegistry();
				TypeCoercer typeCoercer = registry.getService(TypeCoercer.class);
				JdbcTemplate jdbcTemplate = registry.getService(JdbcTemplate.class);
				AutoGenerateStrategy strategy = new QueryAutoGenerateStrategy(jdbcTemplate, typeCoercer, sql, type);
				return new AutoGenerateSourceContribution().withAutoGenerateStrategy(entityName, propertyName, strategy);
			}
		};
		return withContribution(AutoGenerateSource.class, builder);
	}
	
	/**
	 * Sets a property on the builder. These properties are accessible to {@link ServiceBuilder}s
	 * @param name The property name
	 * @param value The property value
	 * @return this TaderBuilder for further configuration
	 */
	public TaderBuilder withProperty(String name, String value) {
		ServiceModule module = new ServiceModule() {
			@Override
			public void bind(ServiceBinder binder) {
				binder.mappedContribution(PropertySource.class, String.valueOf(nextContributionId.getAndIncrement()), name, value);
			}
		};
		registryBuilder.withModule(module);
		return this;
	}
	
	/**
	 * Builds the service registry and returns the {@link Tader} service
	 * @return A {@link Tader} instance
	 */
	public Tader build() {
		return buildRegistry().getService(Tader.class);
	}

	protected ServiceRegistry buildRegistry() {
		return registryBuilder.build();
	}
}
