package org.grater;

import org.grater.internal.GraterUtils;
import org.grater.model.Column;

public class CamelCaseColumnNameResolver implements ColumnNameResolver {
	@Override
	public String resolveName(Column column) {
		return GraterUtils.toCamelCase(column.getName());
	}
}
