package org.tader.jdbc;

import org.tader.PropertyDef;
import org.tader.TypeCoercer;

public class InsertHandlerSourceImpl implements InsertHandlerSource {
	private final InsertHandler defaultHandler;
	
	public InsertHandlerSourceImpl(TypeCoercer typeCoercer, BlobTypeAnalyzer blobAnalyzer) {
		this.defaultHandler = new DefaultInsertHandler(typeCoercer, blobAnalyzer);
	}

	@Override
	public InsertHandler get(PropertyDef propDef) {
		return defaultHandler;
	}
}
