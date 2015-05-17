package org.grater.jdbc;

import org.grater.PropertyDef;

public interface BlobTypeAnalyzer {
	boolean isBlob(PropertyDef propDef);
}
