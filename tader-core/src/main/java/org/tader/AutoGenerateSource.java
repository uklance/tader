package org.tader;

import java.util.Set;

public interface AutoGenerateSource {
	Set<String> getAutoGeneratePropertyNames(String entityName);
	int getNextIncrement(String entityName);
	AutoGenerateStrategy getAutoGenerateStrategy(PropertyDef propDef);
}
