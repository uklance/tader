package org.grater;

import org.grater.model.Column;
import org.grater.model.Table;

public interface ValueParser {
	Object parseValue(Table table, Column column, Object value);
}
