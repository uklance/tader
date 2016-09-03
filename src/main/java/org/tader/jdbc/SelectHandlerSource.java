package org.tader.jdbc;

import org.tader.PropertyDef;

public interface SelectHandlerSource {
	SelectHandler get(PropertyDef propDef);
}
