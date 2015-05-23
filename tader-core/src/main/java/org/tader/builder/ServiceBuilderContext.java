package org.tader.builder;

import java.util.Collection;

public interface ServiceBuilderContext {
	<T> T getService(Class<T> serviceInterface);
	<T> Collection<T> getContributions(Class<T> contributionType);
	String getProperty(String name);
}
