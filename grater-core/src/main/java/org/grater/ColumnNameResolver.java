package org.grater;

import org.grater.model.Column;

public interface ColumnNameResolver {
	public String resolveName(Column column);
}
