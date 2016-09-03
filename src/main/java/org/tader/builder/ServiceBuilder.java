package org.tader.builder;

public interface ServiceBuilder<T> {
	T build(ServiceBuilderContext context);
}
