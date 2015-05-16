package org.grater.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionCallback<T> {
	T handle(Connection con) throws SQLException;
}
