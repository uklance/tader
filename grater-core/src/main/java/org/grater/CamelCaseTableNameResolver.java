package org.grater;

import org.grater.internal.GraterUtils;
import org.grater.model.Table;

public class CamelCaseTableNameResolver implements TableNameResolver {
	@Override
	public String resolveName(Table table) {
		return GraterUtils.toCamelCase(table.getName());
	}
}
