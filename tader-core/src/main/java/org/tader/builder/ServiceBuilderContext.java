package org.tader.builder;

import java.util.Collection;

public interface ServiceBuilderContext {
	<T> T getService(Class<T> serviceInterface);
	<T> Collection<T> getContributions(Class<?> serviceType, Class<T> contributionType);
	String getProperty(String name);
}
