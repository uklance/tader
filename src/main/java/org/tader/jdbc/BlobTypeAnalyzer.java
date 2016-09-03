package org.tader.jdbc;

import org.tader.PropertyDef;

public interface BlobTypeAnalyzer {
	boolean isBlob(PropertyDef propDef);
}
