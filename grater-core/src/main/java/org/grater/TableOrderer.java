package org.grater;

import java.util.Collection;
import java.util.List;

import org.grater.model.Table;

public interface TableOrderer {
	List<Table> getTableOrder(Collection<Table> tables);
}
