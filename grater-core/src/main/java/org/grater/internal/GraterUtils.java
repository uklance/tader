package org.grater.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GraterUtils {
	public static <T> Collection<T> asImmutableCollection(Collection<T> coll) {
		return coll == null || coll.isEmpty() ?
				Collections.<T> emptySet() :
				Collections.unmodifiableCollection(coll);
	}

	public static <T> List<T> asImmutableList(List<T> list) {
		return list == null || list.isEmpty() ?
				Collections.<T> emptyList() :
				Collections.unmodifiableList(list);
	}
	
	public static <K, V> Map<K, V> asMap(Class<K> keyType, Class<V> valueType, Object... keysAndValues) {
		if (keysAndValues.length % 2 != 0) {
			throw new IllegalArgumentException(
					String.format("Keys and values length is not a multiple of 2 (%s)", keysAndValues.length)
			);
		}
		int size = keysAndValues.length / 2;
		Map<K, V> map = new LinkedHashMap<K, V>(size);
		for (int i = 0; i < keysAndValues.length; i += 2) {
			K key = keyType.cast(keysAndValues[i]);
			V value = valueType.cast(keysAndValues[i + 1]);
			
			map.put(key,  value);
		}
		return map;
	}
	
	public static String join(Collection<String> strings, String separator) {
		StringBuilder builder = new StringBuilder();
		boolean isFirst = true;
		for (String s : strings) {
			if (isFirst) {
				isFirst = false;
			} else {
				builder.append(separator);
			}
			builder.append(s);
		}
		return builder.toString();
	}
}
