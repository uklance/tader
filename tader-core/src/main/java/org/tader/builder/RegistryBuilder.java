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

@SuppressWarnings({ "unchecked", "rawtypes" })
public class RegistryBuilder {
	private Map<Class, ServiceBuilder> serviceBuilders = new LinkedHashMap<Class, ServiceBuilder>();
	private Map<String, String> properties = new LinkedHashMap<String, String>();
	private Map<Class, Collection<Object>> contributionMap = new LinkedHashMap<Class, Collection<Object>>();
	private final ClassLoader classloader = RegistryBuilder.class.getClassLoader();

	public <T> RegistryBuilder withServiceBuilder(Class<T> serviceInterface, ServiceBuilder<T> builder) {
		serviceBuilders.put(serviceInterface, builder);
		return this;
	}

	public <I, T extends I> RegistryBuilder withServiceInstance(Class<I> serviceInterface, Class<T> serviceType) {
		return withServiceBuilder(serviceInterface, new ConstructorServiceBuilder<I, T>(serviceInterface, serviceType));
	}

	public <I, T extends I> RegistryBuilder withService(Class<I> serviceInterface, T service) {
		return withServiceBuilder(serviceInterface, new StaticServiceBuilder<I>(service));
	}

	public RegistryBuilder withContribution(Class serviceInterface, Object contribution) {
		Collection<Object> collection = contributionMap.get(serviceInterface);
		if (collection == null) {
			collection = new ArrayList<Object>();
			contributionMap.put(serviceInterface, collection);
		}
		collection.add(contribution);
		return this;
	}

	public RegistryBuilder withProperty(String name, String value) {
		properties.put(name, value);
		return this;
	}

	public Registry build() {
		final Map<Class, Object> proxies = new LinkedHashMap<Class, Object>();

		return new Registry() {
			@Override
			public <T> T getService(Class<T> serviceInterface) {
				if (proxies.containsKey(serviceInterface)) {
					return serviceInterface.cast(proxies.get(serviceInterface));
				}
				ServiceBuilderContext context = createServiceBuilderContext(serviceInterface, this);
				T proxy = createProxy(serviceInterface, context);
				proxies.put(serviceInterface, proxy);
				return proxy;
			}
		};
	}

	private <I> ServiceBuilderContext createServiceBuilderContext(final Class<I> serviceInterface, final Registry registry) {
		return new ServiceBuilderContext() {
			@Override
			public <T> T getService(Class<T> serviceInterface) {
				return registry.getService(serviceInterface);
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
				Collection<T> contributions = (Collection<T>) contributionMap.get(serviceInterface);
				return contributions == null ? Collections.<T> emptyList() : contributions;
			}
		};
	}

	private <T> T createProxy(Class<T> serviceInterface, final ServiceBuilderContext context) {
		if (!serviceBuilders.containsKey(serviceInterface)) {
			throw new RuntimeException("No service builder registered for " + serviceInterface.getName());
		}
		final ServiceBuilder<T> builder = (ServiceBuilder<T>) serviceBuilders.get(serviceInterface);
		final Object[] mutableInstance = new Object[] { null };
		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object target, Method method, Object[] args) throws Throwable {
				Object instance = mutableInstance[0];
				if (instance == null) {
					instance = builder.build(context);
					mutableInstance[0] = instance;
				}
				try {
					return method.invoke(instance, args);
				} catch (InvocationTargetException e) {
					throw e.getTargetException();
				}
			}
		};
		Object proxy = Proxy.newProxyInstance(classloader, new Class[] { serviceInterface }, handler);
		return serviceInterface.cast(proxy);
	}
}
