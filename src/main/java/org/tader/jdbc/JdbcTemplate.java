package org.tader.jdbc;

public interface JdbcTemplate {
	<T> T execute(ConnectionCallback<T> callback);
}
