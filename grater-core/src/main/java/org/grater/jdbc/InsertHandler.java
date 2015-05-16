package org.grater.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.grater.PropertyDef;

public interface InsertHandler {

	void onColumnsSql(StringBuilder columnsSql, boolean isFirst, PropertyDef propDef);

	void onValuesSql(StringBuilder valuesSql, boolean isFirst, PropertyDef propDef);

	void onPreparedStatement(PreparedStatement ps, int index, PropertyDef propDef, Object value) throws SQLException;

	Object getGeneratedKey(ResultSet rs, PropertyDef propDef) throws SQLException;
}
