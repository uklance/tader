package org.grater.jdbc;

public class TestJdbcTemplate implements JdbcTemplate {
	private JdbcTemplate delegate;
	private DatabaseVendor vendor;

	public TestJdbcTemplate(DatabaseVendor vendor, JdbcTemplate delegate) {
		super();
		this.vendor = vendor;
		this.delegate = delegate;
	}

	@Override
	public <T> T execute(ConnectionCallback<T> callback) {
		return delegate.execute(callback);
	}

	public DatabaseVendor getDatabaseVendor() {
		return vendor;
	}
}
