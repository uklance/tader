package org.tader.builder;

public class ConstantContributionBuilder<T> implements ContributionBuilder<T> {
	private final T value;
	
	public ConstantContributionBuilder(T value) {
		super();
		this.value = value;
	}

	@Override
	public T build(ContributionBuilderContext context) {
		return value;
	}
}
