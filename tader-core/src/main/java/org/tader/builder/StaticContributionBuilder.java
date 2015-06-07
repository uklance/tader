package org.tader.builder;

public class StaticContributionBuilder<T> implements ContributionBuilder<T> {
	private final T value;
	
	public StaticContributionBuilder(T value) {
		super();
		this.value = value;
	}

	@Override
	public T build(ContributionBuilderContext context) {
		return value;
	}
}
