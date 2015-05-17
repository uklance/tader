package org.grater.jdbc;

import java.sql.Types;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.grater.PropertyDef;

public class BlobTypeAnalyzerImpl implements BlobTypeAnalyzer {
	private static final Set<Integer> BLOB_TYPES = new HashSet<Integer>(Arrays.asList(Types.BLOB, Types.BINARY));

	@Override
	public boolean isBlob(PropertyDef propDef) {
		return BLOB_TYPES.contains(propDef.getSqlType());
	}
}
