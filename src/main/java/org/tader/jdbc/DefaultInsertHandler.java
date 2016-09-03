package org.tader.jdbc;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.tader.PropertyDef;
import org.tader.TypeCoercer;

public class DefaultInsertHandler implements InsertHandler {
	private final BlobTypeAnalyzer blobAnalyzer;
	private final TypeCoercer typeCoercer;
	
	public DefaultInsertHandler(TypeCoercer typeCoercer, BlobTypeAnalyzer blobAnalyzer) {
		super();
		this.typeCoercer = typeCoercer;
		this.blobAnalyzer = blobAnalyzer;
	}

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
		if (value == null) {
			ps.setNull(index, propDef.getSqlType());
		} else if (blobAnalyzer.isBlob(propDef)) {
			ps.setBlob(index, typeCoercer.coerce(value, InputStream.class));
		} else {
			ps.setObject(index, value);
		}
	}

	@Override
	public Object getGeneratedKey(ResultSet rs, PropertyDef propDef) throws SQLException {
		return rs.getObject(1);
	}
}
