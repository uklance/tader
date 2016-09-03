package org.tader.jdbc;

public enum DatabaseVendor {
	H2("org.h2.Driver", "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1"),
	HSQLDB("org.hsqldb.jdbcDriver", "jdbc:hsqldb:mem:%s"),
	DERBY("org.apache.derby.jdbc.EmbeddedDriver", "jdbc:derby:memory:%s;create=true");
	
	private String className;
	private String urlTemplate;
	private DatabaseVendor(String className, String urlTemplate) {
		this.className = className;
		this.urlTemplate = urlTemplate;
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getUrlTemplate() {
		return urlTemplate;
	}
}
