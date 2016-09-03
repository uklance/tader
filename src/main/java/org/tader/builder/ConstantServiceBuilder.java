package org.tader.builder;

public class ConstantServiceBuilder<T> implements ServiceBuilder<T> {
	private T service;

	public ConstantServiceBuilder(T service) {
		super();
		this.service = service;
	}
	
	@Override
	public T build(ServiceBuilderContext context) {
		return service;
	}
}
