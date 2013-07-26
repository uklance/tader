package org.grater.model.internal;

import org.grater.model.ForeignKey;

public class ForeignKeyImpl implements ForeignKey {
	private final String column;
	private final String referenceTable;
	private final String referenceColumn;
	
	public ForeignKeyImpl(String column, String referenceTable, String referenceColumn) {
		super();
		this.column = column;
		this.referenceTable = referenceTable;
		this.referenceColumn = referenceColumn;
	}
	@Override
	public String getColumn() {
		return column;
	}
	@Override
	public String getReferenceTable() {
		return referenceTable;
	}
	@Override
	public String getReferenceColumn() {
		return referenceColumn;
	}
}
