package org.grater;

import java.sql.Connection;

public interface ConnectionSource {
	Connection getConnection();
	void returnConnection(Connection con);
}
