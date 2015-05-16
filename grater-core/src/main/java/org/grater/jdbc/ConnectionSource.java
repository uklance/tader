package org.grater.jdbc;

import java.sql.Connection;

public interface ConnectionSource {

	Connection getConnection();

	void returnConnection(Connection con);

}
