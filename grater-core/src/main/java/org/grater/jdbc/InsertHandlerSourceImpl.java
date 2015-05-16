package org.grater.jdbc;

import org.grater.PropertyDef;

public class InsertHandlerSourceImpl implements InsertHandlerSource {
	private static final InsertHandler DEFAULT_HANDLER = new DefaultInsertHandler();

	@Override
	public InsertHandler get(PropertyDef propDef) {
		return DEFAULT_HANDLER;
	}
}
