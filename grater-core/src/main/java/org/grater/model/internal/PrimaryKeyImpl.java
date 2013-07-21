package org.grater.model.internal;

import java.util.List;

import org.grater.internal.GraterUtils;
import org.grater.model.PrimaryKey;

public class PrimaryKeyImpl implements PrimaryKey {
	private final List<String> columns;

	public PrimaryKeyImpl(List<String> columns) {
		super();
		this.columns = GraterUtils.asImmutableList(columns);
	}
	
	public List<String> getColumns() {
		return columns;
	}
}
