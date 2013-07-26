package org.grater.model;


public interface Column {
	String getName();
	int getType();
	boolean isNullable();
	int getSize();
}
