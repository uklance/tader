package org.grater.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.grater.DataSourceSchemaSource;
import org.grater.Entity;
import org.grater.Grater;
import org.grater.GraterBuilder;
import org.grater.SchemaSource;
import org.hibernate.dialect.H2Dialect;
import org.junit.Assert;
import org.junit.Test;

public class GraterBuilderIntegrationTest {

	@Test
	public void test() throws SQLException {
		DataSource ds = createDataSource();
		Connection con = ds.getConnection();
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
		con.close();

		// reverse engineer the schema model from the DataSource
		SchemaSource schemaSource = new DataSourceSchemaSource(ds, new H2Dialect());

		// initialize grater
		Grater grater = new GraterBuilder()
			.withSchemaSource(schemaSource)
			.withDataSource(ds)
			.withDialect(new H2Dialect())
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
	}
	
	protected DataSource createDataSource() {
		String driverClassName = "org.h2.Driver";
		String url = "jdbc:h2:mem:test";
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(driverClassName);
		ds.setUrl(url);
		//ds.setUsername(user);
		//ds.setPassword(password);
		return ds;
	}
}
