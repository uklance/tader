package org.tader.jdbc;

import org.tader.PropertyDef;

public interface InsertHandlerSource {
	InsertHandler get(PropertyDef propDef);
}
