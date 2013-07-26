package org.grater;

import java.util.Map;

public interface PartialEntity {
	String getTable();
	Map<String, Object> getValues();
}
