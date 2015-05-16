package org.grater.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SimpleConnectionSource implements ConnectionSource {
	private String url;
	private String user;
	private String password;

	public SimpleConnectionSource(String className, String url) {
		this(className, url, null, null);
	}

	public SimpleConnectionSource(String className, String url, String user, String password) {
		super();
		this.url = url;
		this.user = user;
		this.password = password;

		try {
			Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Connection getConnection() {
		try {
			if (user == null && password == null) {
				return DriverManager.getConnection(url);
			}
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void returnConnection(Connection con) {
		try {
			con.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
