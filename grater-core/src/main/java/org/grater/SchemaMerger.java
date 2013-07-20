package org.grater;

import java.util.List;

import org.grater.model.Schema;

public interface SchemaMerger {
	Schema merge(List<Schema> schemas, MergeMode mergeMode);
}
