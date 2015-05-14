package org.grater;

import java.util.Set;

public interface AutoGenerateSource {
	Set<String> getAutoGeneratePropertyNames(String entityName);
	int getNextIncrement(String entityName);
}
