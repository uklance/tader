package org.tader.builder;

public interface ContributionBuilderContext {
	<T> T getService(Class<T> type);
}
