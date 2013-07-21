package org.grater.model.internal;

import org.grater.model.ForeignKey;

public class ForeignKeyImpl implements ForeignKey {
	private final String column;
	private final String referenceColumn;
	public ForeignKeyImpl(String column, String referenceColumn) {
		super();
		this.column = column;
		this.referenceColumn = referenceColumn;
	}
	public String getColumn() {
		return column;
	}
	public String getReferenceColumn() {
		return referenceColumn;
	}
}
