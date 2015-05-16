package org.grater.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.grater.PropertyDef;

public class DefaultSelectHandler implements SelectHandler {

	@Override
	public void onColumnsSql(StringBuilder columnsSql, boolean isFirst, PropertyDef propDef) {
		if (!isFirst) {
			columnsSql.append(", ");
		}
		columnsSql.append(propDef.getColumnName());
	}

	@Override
	public void onWhereSql(StringBuilder whereSql, boolean isFirst, PropertyDef propDef) {
		if (!isFirst) {
			whereSql.append(" AND ");
		}
		whereSql.append(propDef.getColumnName());
		whereSql.append(" = ?");
	}

	@Override
	public void onPreparedStatement(PreparedStatement ps, int index, PropertyDef propDef, Object value) throws SQLException {
		ps.setObject(index, value, propDef.getSqlType());
	}

	@Override
	public Object getValue(ResultSet rs, int index, PropertyDef propDef) throws SQLException {
		return rs.getObject(index);
	}
}
