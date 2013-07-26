package org.grater;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.grater.model.Column;
import org.grater.model.Schema;
import org.grater.model.Table;
import org.junit.Test;

public class ReverseEngineerSchemaSourceTest {
	@Test
	public void testGetSchema() throws SQLException{
		Connection con = getConnection();
		try {
			initializeDatabase(con);
			
			ConnectionSource cs = new SingletonConnectionSource(con);
			
			SchemaSource schemaSource = new ReverseEngineerSchemaSource(cs, "test", null);
			Schema schema = schemaSource.getSchema();
			
			//assertEquals(2, schema.getTables().size());
			Table authorTable = getTable(schema, "author");
			assertEquals(2, authorTable.getColumns().size());
			Column authorName = getColumn(authorTable, "name");
			assertEquals(50, authorName.getSize());
	
			Table bookTable = getTable(schema, "book");
			assertEquals(3, bookTable.getColumns().size());
			Column bookName = getColumn(bookTable, "name");
			assertEquals(60, bookName.getSize());
		} finally {
			con.close();
		}
	}

	protected Table getTable(Schema schema, String name) {
		for (Table table : schema.getTables()) {
			if (table.getName().toLowerCase().equals(name.toLowerCase())) {
				return table;
			}
		}
		fail("No such table " + name);
		return null;
	}

	protected Column getColumn(Table table, String name) {
		for (Column column : table.getColumns()) {
			if (column.getName().toLowerCase().equals(name.toLowerCase())) {
				return column;
			}
		}
		fail("No such column " + name);
		return null;
	}
	
	protected void initializeDatabase(Connection con) throws SQLException {
		Statement statement = con.createStatement();

		// initialize the schema
		statement.executeUpdate(
		   "create table author (" +
		      "author_id int identity(1,1) not null, " +
		      "name varchar(50) not null, " +
		      "constraint pk_author primary key (author_id)" +
		    ")"
		);
		statement.executeUpdate(
		   "create table book (" +
		      "book_id int identity(1,1) not null, " +
		      "author_id int not null, " +
		      "name varchar(60) not null, " +
		      "constraint pk_book primary key (book_id), " +
		      "constraint fk_book_author foreign key (author_id) references author(author_id)" +
		    ")"
		);
		statement.close();
	}
	
	protected Connection getConnection() {
		String driverClassName = "org.h2.Driver";
		String url = "jdbc:h2:mem:test";
		String user = null;
		String password = null;
		
		return new SimpleConnectionSource(driverClassName, url, user, password).getConnection();
	}
}
