package org.grater.jdbc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.grater.TypeCoercer;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class TypeCoercerImpl implements TypeCoercer {
	private final Map<String, TypeCoercerContribution> contributionMap;

	public TypeCoercerImpl(Collection<TypeCoercerContribution<?, ?>> contributions) {
		contributionMap = new HashMap<String, TypeCoercerContribution>();
		for (TypeCoercerContribution<?, ?> contribution : contributions) {
			String key = createKey(contribution.getSourceType(), contribution.getTargetType());
			contributionMap.put(key, contribution);
		}
	}

	@Override
	public <T> T coerce(Object source, Class<T> targetType) {
		if (source == null) {
			return null;
		}
		Class<?> sourceType = source.getClass();
		if (sourceType == targetType) {
			return targetType.cast(source);
		}
		String key = createKey(sourceType, targetType);
		TypeCoercerContribution contribution = contributionMap.get(key);
		if (contribution == null) {
			throw new RuntimeException("No coercion for " + key);
		}
		return targetType.cast(contribution.coerce(source));
	}

	private String createKey(Class<?> sourceType, Class<?> targetType) {
		return sourceType.getName() + "->" + targetType.getName();
	}
}
