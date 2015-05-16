package org.grater;

public interface TypeCoercer {
	<T> T coerce(Object o, Class<T> type);
}
