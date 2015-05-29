package org.tader.builder;

import java.util.Set;

@SuppressWarnings("rawtypes")
public interface Registry {
	<T> T getService(Class<T> serviceInterface);
	Set<Class> getServiceInterfaces();
}
