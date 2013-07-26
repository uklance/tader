package org.grater.model;

public interface ForeignKey {
	String getColumn();
	String getReferenceColumn();
	String getReferenceTable();
}
