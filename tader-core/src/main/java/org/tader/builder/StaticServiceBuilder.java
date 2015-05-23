package org.tader.builder;

public class StaticServiceBuilder<T> implements ServiceBuilder<T> {
	private T service;

	public StaticServiceBuilder(T service) {
		super();
		this.service = service;
	}
	
	@Override
	public T build(ServiceBuilderContext context) {
		return service;
	}
}
