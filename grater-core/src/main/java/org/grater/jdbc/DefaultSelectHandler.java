package org.grater.jdbc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.grater.PropertyDef;

public class DefaultSelectHandler implements SelectHandler {
	private final BlobTypeAnalyzer blobAnalyzer;
	
	public DefaultSelectHandler(BlobTypeAnalyzer blobAnalyzer) {
		super();
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
		if (blobAnalyzer.isBlob(propDef)) {
			return getByteArray(rs, index);
		} else {
			return rs.getObject(index);
		}
	}

	protected Object getByteArray(ResultSet rs, int index) throws SQLException {
		InputStream in = rs.getBinaryStream(index);
		try {
			byte[] bytes = new byte[1024];
			int count;
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			while ((count = in.read(bytes)) > 0) {
				out.write(bytes, 0, count);
			}
			in.close();
			return out.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
