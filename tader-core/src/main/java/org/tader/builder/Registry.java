package org.tader.builder;

public interface Registry {
	<T> T getService(Class<T> serviceInterface);
}
