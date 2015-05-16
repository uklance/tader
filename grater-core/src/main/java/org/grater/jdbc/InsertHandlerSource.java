package org.grater.jdbc;

import org.grater.PropertyDef;

public interface InsertHandlerSource {
	InsertHandler get(PropertyDef propDef);
}
