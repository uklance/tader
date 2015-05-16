package org.grater.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.grater.PropertyDef;

public class DefaultInsertHandler implements InsertHandler {

	@Override
	public void onColumnsSql(StringBuilder columnsSql, boolean isFirst, PropertyDef propDef) {
		if (!isFirst) {
			columnsSql.append(", ");
		}
		columnsSql.append(propDef.getColumnName());
	}

	@Override
	public void onValuesSql(StringBuilder valuesSql, boolean isFirst, PropertyDef propDef) {
		if (!isFirst) {
			valuesSql.append(", ");
		}
		valuesSql.append("?");
	}

	@Override
	public void onPreparedStatement(PreparedStatement ps, int index, PropertyDef propDef, Object value) throws SQLException {
		ps.setObject(index, value);
	}

	@Override
	public Object getGeneratedKey(ResultSet rs, PropertyDef propDef) throws SQLException {
		return rs.getObject(1);
	}
}
