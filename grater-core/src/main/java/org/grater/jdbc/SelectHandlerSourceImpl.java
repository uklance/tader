package org.grater.jdbc;

import org.grater.PropertyDef;

public class SelectHandlerSourceImpl implements SelectHandlerSource {
	private static final SelectHandler DEFAULT_HANDLER = new DefaultSelectHandler();
	
	@Override
	public SelectHandler get(PropertyDef propDef) {
		return DEFAULT_HANDLER;
	}
}
