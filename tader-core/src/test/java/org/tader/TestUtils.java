package org.tader;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
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

	public static List<TestJdbcTemplate> getTestJdbcTemplates() {
		List<TestJdbcTemplate> templates = new ArrayList<TestJdbcTemplate>();
		for (DatabaseVendor vendor : DatabaseVendor.values()) {
			String id = "TestUtils" + nextId.getAndIncrement();
			String url = String.format(vendor.getUrlTemplate(), id);

			ConnectionSource connectionSource = new SimpleConnectionSource(vendor.getClassName(), url);
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

	public static void createTableBook(TestJdbcTemplate template) {
		executePropertySql(template, "createTableBook");
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

	private static String getProperty(DatabaseVendor vendor, String propName) {
		String vendorProp = vendor.name() + "." + propName;

		String value = properties.getProperty(vendorProp);

		if (value == null) {
			value = properties.getProperty(propName);

			if (value == null) {
				throw new RuntimeException("No such property " + vendorProp);
			}
		}

		return value;
	}

	public static Map<String, Object> createMap(Object... keysAndValues) {
		if (keysAndValues.length %2 != 0) {
			throw new RuntimeException();
		}
		
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (int i =0; i < keysAndValues.length; i+=2) {
			map.put((String) keysAndValues[i], keysAndValues[i+1]);
		}
		return map;
	}
}
