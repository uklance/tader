package org.grater.jdbc;

public interface JdbcTemplate {
	<T> T execute(ConnectionCallback<T> callback);
}
