package org.grater;

import org.grater.model.Column;
import org.grater.model.Table;

public interface ValueGenerator {
	Object generateValue(Table table, Column column, IncrementProvider incProvider);
}
