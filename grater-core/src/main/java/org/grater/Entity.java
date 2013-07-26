package org.grater;

import java.util.Map;

public interface Entity {
	String getTable();
	Map<String, Object> getValues();
}
