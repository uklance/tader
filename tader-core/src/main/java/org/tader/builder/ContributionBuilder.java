package org.tader.builder;

public interface ContributionBuilder<T> {
	T build(ContributionBuilderContext context);
}
