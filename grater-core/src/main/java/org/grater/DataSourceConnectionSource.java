package org.grater;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public class DataSourceConnectionSource implements ConnectionSource {
	private final DataSource dataSource;
	
	public DataSourceConnectionSource(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}

	@Override
	public Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			throw new GraterException(e);
		}
	}

	@Override
	public void returnConnection(Connection con) {
		try {
			con.close();
		} catch (SQLException e) {
			throw new GraterException(e);
		}
	}
}
