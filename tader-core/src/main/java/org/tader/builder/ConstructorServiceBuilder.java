package org.tader.builder;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ConstructorServiceBuilder<I, T extends I> implements ServiceBuilder<I> {
	private final Class<I> serviceInterface;
	private final Class<T> serviceClass;
	
	public ConstructorServiceBuilder(Class<I> serviceInterface, Class<T> serviceClass) {
		super();
		this.serviceInterface = serviceInterface;
		this.serviceClass = serviceClass;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public I build(ServiceBuilderContext context) {
		Constructor[] constructors = serviceClass.getConstructors();
		if (constructors.length != 1) {
			throw new RuntimeException(String.format("Found %s constructor(s) for %s, expecting 1", constructors.length,
					serviceClass.getName()));
		}
		try {
			Constructor constructor = constructors[0];
			List<Object> params = new ArrayList<Object>(constructor.getParameterTypes().length);
			for (Class paramType : constructor.getParameterTypes()) {
				params.add(context.getService(paramType));
			}
		
			return serviceInterface.cast(constructor.newInstance(params.toArray()));
		} catch (Exception e) {
			throw new RuntimeException("Error building " + serviceClass.getName(), e);
		}
	}
}
