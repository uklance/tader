package org.tader;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.tader.jdbc.ConnectionCallback;
import org.tader.jdbc.ConnectionSource;
import org.tader.jdbc.DatabaseVendor;
import org.tader.jdbc.JdbcTemplate;
import org.tader.jdbc.JdbcTemplateImpl;
import org.tader.jdbc.SimpleConnectionSource;
import org.tader.jdbc.TestJdbcTemplate;

public class TestUtils {
	private static final AtomicInteger nextId = new AtomicInteger(0);

	private static final Properties properties = loadProperties();

	public static ConnectionSource newConnectionSource(DatabaseVendor vendor) {
		String id = "TestUtils" + nextId.getAndIncrement();
		String url = String.format(vendor.getUrlTemplate(), id);

		return new SimpleConnectionSource(vendor.getClassName(), url);
	}

	public static List<TestJdbcTemplate> getTestJdbcTemplates() {
		List<TestJdbcTemplate> templates = new ArrayList<TestJdbcTemplate>();
		for (DatabaseVendor vendor : DatabaseVendor.values()) {
			ConnectionSource connectionSource = newConnectionSource(vendor);
			JdbcTemplate delegate = new JdbcTemplateImpl(connectionSource);

			templates.add(new TestJdbcTemplate(vendor, delegate));
		}
		return templates;
	}

	private static Properties loadProperties() {
		String path = TestUtils.class.getName().replace(".", "/") + ".properties";
		Properties props = new Properties();
		InputStream in = TestUtils.class.getClassLoader().getResourceAsStream(path);
		if (in == null) {
			throw new RuntimeException("No such resource " + path);
		}
		try {
			props.load(in);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return props;
	}

	public static void createTableAuthor(TestJdbcTemplate template) {
		executePropertySql(template, "createTableAuthor");
	}

	public static void createTableAuthor(ConnectionSource connectionSource) {
		executePropertySql(connectionSource, "createTableAuthor");
	}

	public static void createTableBook(TestJdbcTemplate template) {
		executePropertySql(template, "createTableBook");
	}

	public static void createTableBook(ConnectionSource connectionSource) {
		executePropertySql(connectionSource, "createTableBook");
	}

	public static void createTableHasIdentity(TestJdbcTemplate template) {
		executePropertySql(template, "createTableHasIdentity");
	}

	public static void createTableHasBlob(TestJdbcTemplate template) {
		executePropertySql(template, "createTableHasBlob");
	}

	private static void executePropertySql(TestJdbcTemplate template, String propName) {
		final String sql = getProperty(template.getDatabaseVendor(), propName);
		ConnectionCallback<Void> callback = new ConnectionCallback<Void>() {
			@Override
			public Void handle(Connection con) throws SQLException {
				con.createStatement().execute(sql);
				return null;
			}
		};
		template.execute(callback);
	}

	private static void executePropertySql(ConnectionSource connectionSource, String propName) {
		JdbcTemplate template = new JdbcTemplateImpl(connectionSource);
		final String sql = getProperty(propName, false);
		ConnectionCallback<Void> callback = new ConnectionCallback<Void>() {
			@Override
			public Void handle(Connection con) throws SQLException {
				con.createStatement().execute(sql);
				return null;
			}
		};
		template.execute(callback);
	}

	private static String getProperty(DatabaseVendor vendor, String propName) {
		String value = getProperty(vendor.name() + "." + propName, true);
		if (value == null) {
			return getProperty(propName, false);
		}
		return value;
	}

	private static String getProperty(String propName, boolean allowNull) {
		String value = properties.getProperty(propName);

		if (value == null && !allowNull) {
			throw new RuntimeException("No such property " + propName);
		}

		return value;
	}

	public static Map<String, Object> createMap(Object... keysAndValues) {
		if (keysAndValues.length % 2 != 0) {
			throw new RuntimeException();
		}

		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (int i = 0; i < keysAndValues.length; i += 2) {
			map.put((String) keysAndValues[i], keysAndValues[i + 1]);
		}
		return map;
	}

	public static int getAuthorCount(ConnectionSource connectionSource) {
		return queryForInt(new JdbcTemplateImpl(connectionSource), "SELECT COUNT(*) FROM AUTHOR");
	}

	public static int queryForInt(JdbcTemplate template, final String sql) {
		ConnectionCallback<Integer> callback = new ConnectionCallback<Integer>() {
			@Override
			public Integer handle(Connection con) throws SQLException {
				ResultSet rs = con.createStatement().executeQuery(sql);
				rs.next();
				return rs.getInt(1);
			}
		};
		return template.execute(callback);
	}
}
