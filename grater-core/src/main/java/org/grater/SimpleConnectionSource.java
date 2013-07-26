package org.grater;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleConnectionSource implements ConnectionSource {
	private String driverClassName;
	private String url;
	private String user;
	private String password;

	public SimpleConnectionSource(String driverClassName, String url, String user, String password) {
		super();
		this.driverClassName = driverClassName;
		this.url = url;
		this.user = user;
		this.password = password;
	}

	@Override
	public Connection getConnection() {
		try {
			Class.forName(driverClassName);
			return DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
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
