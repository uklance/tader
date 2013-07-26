package org.grater;

import java.sql.Connection;
import java.util.Map;

import org.grater.model.TableModel;

public interface GraterDao {
	Map<String, Object> insert(Connection con, TableModel table, Map<String, Object> values);
	Map<String, Object> select(Connection con, TableModel table, Map<String, Object> pk);
	Map<String, Object> delete(Connection con, TableModel table, Map<String, Object> pk);
}
