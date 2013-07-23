Grater helps with generating test data prior to testing DAO's, stored procedures or any other service that requires
relational data. 

How It Works
------------
Grater has knowledge of your relational database model including primary keys, foreign keys and required columns.
It allows you to succintly generate test data by specifying only the fields you are interested in. Any required
fields that were not specified will be generated, including foreign key records. This leaves your test case succint
and free from the noise of populating uninteresting fields.

Example
-------

```java
DataSource ds = createDataSource(user, password, url, className);
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
      "constraint pk_book primary key (author_id), "
      "constraint fk_book_author foreign_key (author_id) references author(author_id)" +
    ")"
);
statement.close();
con.close();

// reverse engineer the schema from the DataSource
SchemaSource schemaSource = new DataSourceSchemaSource(ds);

// initialize grater
Grater grater = new GraterBuilder().withSchemaSource(schemaSource).withDataSource(ds).build();

// insert a book without an author
TableRow row = grater.insert("book", "name", "War and Peace");

// bookId populated by identity
int bookId = row.get("bookId", Integer.class);

// required foreign key is populated
TableRow generatedAuthor = row.get("author", TableRow.class);

// author name is generated
Assert.assertNotNull(generatedAuthor.get("name"));

// author id populated by identity
Assert.assertNotNull(generatedAuthor.get("authorId"));
```
