package org.tader;

public interface TypeCoercer {
	<T> T coerce(Object o, Class<T> type);
}
