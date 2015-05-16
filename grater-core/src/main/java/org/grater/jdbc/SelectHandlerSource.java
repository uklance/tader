package org.grater.jdbc;

import org.grater.PropertyDef;

public interface SelectHandlerSource {
	SelectHandler get(PropertyDef propDef);
}
