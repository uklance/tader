package org.tader.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public class JdbcTemplateImpl implements JdbcTemplate {
	private final ConnectionSource connectionSource;
	
	public JdbcTemplateImpl(ConnectionSource connectionSource) {
		super();
		this.connectionSource = connectionSource;
	}

	@Override
	public <T> T execute(ConnectionCallback<T> callback) {
		Connection con = connectionSource.getConnection();
		
		try {
			return callback.handle(con);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			connectionSource.returnConnection(con);
		}
	}
}
