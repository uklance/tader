package org.grater.jdbc;

public abstract class TypeCoercerContribution<S, T> {
	private final Class<S> sourceType;
	private final Class<T> targetType;

	protected TypeCoercerContribution(Class<S> sourceType, Class<T> targetType) {
		super();
		this.sourceType = sourceType;
		this.targetType = targetType;
	}

	public Class<S> getSourceType() {
		return sourceType;
	}
	
	public Class<T> getTargetType() {
		return targetType;
	}
	
	public abstract T coerce(S source);
}
