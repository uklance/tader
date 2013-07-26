package org.grater;

import java.sql.Connection;

public class SingletonConnectionSource implements ConnectionSource {
	private final Connection connection;
	
	public SingletonConnectionSource(Connection connection) {
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
