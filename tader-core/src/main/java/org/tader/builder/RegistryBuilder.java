package org.tader.builder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple IOC registry builder.
 * Note: The RegistryBuilder itself is not thread-safe, the resultant {@link Registry} is thread-safe once built
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class RegistryBuilder {
	private Map<Class, ServiceBuilder> serviceBuilders = new LinkedHashMap<Class, ServiceBuilder>();
	private Map<String, String> properties = new LinkedHashMap<String, String>();
	private Map<Class, Collection<ContributionBuilder>> contributionBuilders = new LinkedHashMap<Class, Collection<ContributionBuilder>>();
	private final ClassLoader classloader = RegistryBuilder.class.getClassLoader();
	private boolean isBuilt = false;

	public <T> RegistryBuilder withServiceBuilder(Class<T> serviceInterface, ServiceBuilder<T> builder) {
		if (isBuilt) {
			throw new IllegalStateException("Registry has already been built");
		}
		serviceBuilders.put(serviceInterface, builder);
		return this;
	}

	public <I, T extends I> RegistryBuilder withServiceInstance(Class<I> serviceInterface, Class<T> serviceType) {
		return withServiceBuilder(serviceInterface, new ConstructorServiceBuilder<I, T>(serviceInterface, serviceType));
	}

	public <I, T extends I> RegistryBuilder withService(Class<I> serviceInterface, T service) {
		return withServiceBuilder(serviceInterface, new StaticServiceBuilder<I>(service));
	}

	public RegistryBuilder withContribution(Class serviceInterface, final Object contribution) {
		return withContributionBuilder(serviceInterface, new StaticContributionBuilder(contribution));
	}
	
	public RegistryBuilder withContributionBuilder(Class serviceInterface, ContributionBuilder builder) {
		if (isBuilt) {
			throw new IllegalStateException("Registry has already been built");
		}
		Collection<ContributionBuilder> collection = contributionBuilders.get(serviceInterface);
		if (collection == null) {
			collection = new ArrayList<ContributionBuilder>();
			contributionBuilders.put(serviceInterface, collection);
		}
		collection.add(builder);
		return this;
	}

	public RegistryBuilder withProperty(String name, String value) {
		if (isBuilt) {
			throw new IllegalStateException("Registry has already been built");
		}
		properties.put(name, value);
		return this;
	}
	
	private <T> T getOrCreateProxy(
			Class<T> serviceInterface, 
			ConcurrentMap<Class, Object> serviceProxies, 
			ConcurrentMap<Class, Collection> contributions,
			ContributionBuilderContext contributionContext
	) {
		if (serviceProxies.containsKey(serviceInterface)) {
			return serviceInterface.cast(serviceProxies.get(serviceInterface));
		}
		ServiceBuilderContext context = createServiceBuilderContext(serviceInterface, serviceProxies, contributions, contributionContext);
		T proxyCandidate = createProxy(serviceInterface, context);
		T existingProxy = serviceInterface.cast(serviceProxies.putIfAbsent(serviceInterface, proxyCandidate));
		return existingProxy == null ? proxyCandidate : existingProxy;
	}

	public Registry build() {
		if (isBuilt) {
			throw new IllegalStateException("Registry has already been built");
		}
		final ConcurrentMap<Class, Object> serviceProxies = new ConcurrentHashMap<Class, Object>();
		final ConcurrentMap<Class, Collection> contributions = new ConcurrentHashMap<Class, Collection>();
		
		final ContributionBuilderContext contributionContext = new ContributionBuilderContext() {
			@Override
			public <T> T getService(Class<T> serviceInterface) {
				return getOrCreateProxy(serviceInterface, serviceProxies, contributions, this);
			}
		};

		Registry registry = new Registry() {
			@Override
			public <T> T getService(Class<T> serviceInterface) {
				return getOrCreateProxy(serviceInterface, serviceProxies, contributions, contributionContext);
			}
			
			@Override
			public Set<Class> getServiceInterfaces() {
				return Collections.unmodifiableSet(serviceBuilders.keySet());
			}
		};
		isBuilt = true;
		return registry;
	}

	private <I> ServiceBuilderContext createServiceBuilderContext(
			final Class<I> serviceInterface, 
			final ConcurrentMap<Class, Object> serviceProxies, 
			final ConcurrentMap<Class, Collection> contributions,
			final ContributionBuilderContext contributionContext
	) {
		return new ServiceBuilderContext() {
			@Override
			public <T> T getService(Class<T> serviceInterface2) {
				return getOrCreateProxy(serviceInterface2, serviceProxies, contributions, contributionContext);
			}

			@Override
			public String getProperty(String name) {
				if (!properties.containsKey(name)) {
					throw new RuntimeException("No such property " + name);
				}
				return properties.get(name);
			}

			@Override
			public <T> Collection<T> getContributions(Class<T> contributionType) {
				Collection<T> serviceContributions = (Collection<T>) contributions.get(serviceInterface);
				if (serviceContributions == null) {
					Collection<ContributionBuilder> builders = contributionBuilders.get(serviceInterface);
					Collection<T> candidate;
					if (builders == null || builders.isEmpty()) {
						candidate = Collections.<T> emptyList();
					} else {
						candidate = new ArrayList<T>();
						for (ContributionBuilder builder : builders) {
							candidate.add((T) builder.build(contributionContext));
						}
					}
					Collection<T> previous = contributions.putIfAbsent(serviceInterface, candidate);
					serviceContributions = previous == null ? candidate : previous;
				}
				return serviceContributions;
			}
		};
	}

	private <T> T createProxy(Class<T> serviceInterface, final ServiceBuilderContext context) {
		if (!serviceBuilders.containsKey(serviceInterface)) {
			throw new RuntimeException("No service builder registered for " + serviceInterface.getName());
		}
		final ServiceBuilder<T> builder = (ServiceBuilder<T>) serviceBuilders.get(serviceInterface);
		final AtomicReference<T> serviceReference = new AtomicReference<T>();
		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object target, Method method, Object[] args) throws Throwable {
				T service = serviceReference.get();
				if (service == null) {
					T candidate = builder.build(context);
					serviceReference.compareAndSet(null, candidate);
					service = serviceReference.get();
				}
				try {
					return method.invoke(service, args);
				} catch (InvocationTargetException e) {
					throw e.getTargetException();
				}
			}
		};
		Object proxy = Proxy.newProxyInstance(classloader, new Class[] { serviceInterface }, handler);
		return serviceInterface.cast(proxy);
	}
}
