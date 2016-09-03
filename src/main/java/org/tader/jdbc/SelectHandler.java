package org.tader.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.tader.PropertyDef;

public interface SelectHandler {

	void onColumnsSql(StringBuilder columnsSql, boolean isFirst, PropertyDef propDef);

	void onWhereSql(StringBuilder whereSql, boolean isFirst, PropertyDef propDef);

	void onPreparedStatement(PreparedStatement ps, int index, PropertyDef propDef, Object value) throws SQLException;

	Object getValue(ResultSet rs, int index, PropertyDef propDef) throws SQLException;

}
