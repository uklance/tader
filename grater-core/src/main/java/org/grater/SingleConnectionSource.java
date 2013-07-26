package org.grater;

import java.sql.Connection;

public class SingleConnectionSource implements ConnectionSource {
	private final Connection connection;
	
	public SingleConnectionSource(Connection connection) {
		super();
		this.connection = connection;
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public void returnConnection(Connection con) {
		// do nothing
	}
}
