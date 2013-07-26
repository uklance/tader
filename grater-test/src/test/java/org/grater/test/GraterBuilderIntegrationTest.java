package org.grater.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.grater.ConnectionSource;
import org.grater.Entity;
import org.grater.Grater;
import org.grater.GraterBuilder;
import org.grater.ReverseEngineerSchemaSource;
import org.grater.SchemaSource;
import org.grater.SimpleConnectionSource;
import org.grater.SingletonConnectionSource;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.junit.Assert;
import org.junit.Test;

public class GraterBuilderIntegrationTest {

	@Test
	public void test() throws SQLException {
		Connection con = getConnection();
		try {
			initializeDatabase(con);
			
			ConnectionSource cs = new SingletonConnectionSource(con);
			Dialect dialect = new H2Dialect();
	
			// reverse engineer the schema model from the DataSource
			SchemaSource schemaSource = new ReverseEngineerSchemaSource(cs);
	
			// initialize grater
			Grater grater = new GraterBuilder()
				.withSchemaSource(schemaSource)
				.withConnectionSource(cs)
				.withDialect(dialect)
				.build();
	
			// insert a book without an author
			Entity book = grater.insert("book", "name", "War and Peace");
	
			// bookId populated by identity
			int bookId = book.getInteger("bookId");
			Assert.assertTrue(bookId > 0);
	
			// required foreign key is populated
			Entity author = book.getEntity("author");
			Assert.assertNotNull(author);
	
			// author name is generated
			Assert.assertNotNull(author.getString("name"));
	
			// author id populated by identity
			int authorId = author.getInteger("authorId");
			Assert.assertTrue(authorId > 0);
		} finally {
			con.close();
		}
	}
	
	protected void initializeDatabase(Connection con) throws SQLException {
		Statement statement = con.createStatement();

		// initialize the schema
		statement.executeUpdate(
		   "create table author (" +
		      "author_id int identity(1,1) not null, " +
		      "name varchar(255) not null, " +
		      "constraint pk_author primary key (author_id)" +
		    ")"
		);
		statement.executeUpdate(
		   "create table book (" +
		      "book_id int identity(1,1) not null, " +
		      "author_id int not null, " +
		      "name varchar(255) not null, " +
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
