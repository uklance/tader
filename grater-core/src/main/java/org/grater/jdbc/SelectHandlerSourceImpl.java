package org.grater.jdbc;

import org.grater.PropertyDef;

public class SelectHandlerSourceImpl implements SelectHandlerSource {
	private final SelectHandler defaultHandler;
	
	public SelectHandlerSourceImpl(BlobTypeAnalyzer blobAnalyzer) {
		defaultHandler = new DefaultSelectHandler(blobAnalyzer);
	}
	
	@Override
	public SelectHandler get(PropertyDef propDef) {
		return defaultHandler;
	}
}
