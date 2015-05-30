![Tader](https://github.com/uklance/tader/raw/master/tader_250.png)

Tader Test Data [![Build Status](https://travis-ci.org/uklance/tader.svg?branch=master)](https://travis-ci.org/uklance/tader) [![Coverage Status](https://coveralls.io/repos/uklance/tader/badge.svg?branch=master)](https://coveralls.io/r/uklance/tader?branch=master)
---------------

Tader helps with generating test data prior to testing DAO's, stored procedures or any other service that requires
relational data. 

#### How It Works

Tader has knowledge of your relational database model including primary keys, foreign keys and nullable columns.
It allows you to succintly generate test data by specifying only the fields you are interested in. Any required
fields that were not specified will be generated, including foreign key records. This leaves your test case succint
and free from the noise of populating uninteresting fields.

#### Instantiating Tader

The simplest way to get an instance of Tader is via the TaderBuilder

```java
import org.tader.*;
import org.tader.jdbc.*;
import org.tader.builder.*;

ConnectionSource connectionSource = new SimpleConnectionSource(
   "org.h2.Driver", 
   "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
   "user",
   "password"
);

Tader tader = new TaderBuilder()
   .withCoreServices()
   .withCoreJdbcServices()
   .withCoreTypeCoercerContributions()
   .withCoreAutoGenerateSourceContributions()
   .withConnectionSource(connectionSource)
   .withServiceInstance(NameTranslator.class, UpperCamelNameTranslator.class)
   .build();
```
#### Inserting Records

Let's assume we have the following two tables

```sql
CREATE TABLE AUTHOR (
   AUTHOR_ID int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
   AUTHOR_NAME varchar(255) not null,
   DATE_OF_BIRTH date not null,
   AUTHOR_HOBBY varchar(255),
   constraint PK_AUTHOR primary key(AUTHOR_ID)
)
	
CREATE TABLE BOOK (
   BOOK_ID int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
   BOOK_NAME varchar(255) not null,
   AUTHOR_ID int not null,
   constraint PK_BOOK primary key(BOOK_ID),
   constraint FK_BOOK_AUTHOR foreign key(AUTHOR_ID) references AUTHOR(AUTHOR_ID)
)
```

##### Inserting foreign key records implicitly
```java
import static org.junit.Assert.*;
...
PartialEntity bookPartial = new PartialEntity("book");

Entity book = tader.insert(bookPartial);
assertEquals("bookName0", book.getString("bookName"));
assertEquals(1, book.getInteger("bookId").intValue());

Entity author = book.getEntity("authorId");
assertEquals("authorName0", author.getString("authorName"));
assertEquals(1, author.getInteger("authorId").intValue());
```

##### Inserting foreign key records by PartialEntity
```java
PartialEntity authorPartial = new PartialEntity("author")
   .withValue("authorName", "Stephen King");

PartialEntity bookPartial = new PartialEntity("book")
   .withValue("authorId", authorPartial);

Entity book = tader.insert(bookPartial);
assertEquals("bookName0", book.getString("bookName"));
assertEquals(1, book.getInteger("bookId").intValue());

Entity author = book.getEntity("authorId");
assertEquals("Stephen King", author.getString("authorName"));
assertEquals(1, author.getInteger("authorId").intValue());
```

#### AutoGenerateSource / AutoGenerateStrategy

Invoking `TaderBuilder.withCoreAutoGenerateSourceContributions()` will configure auto-generate strategies for the following SQL types:

 * VARCHAR - Property name suffixed with a increment starting at 0 (eg authorName0, authorName1)
 * INTEGER - Integer value starting from 0 incrementing by 1
 * DECIMAL - Decimal value starting from 0.0 incrementing by 1.0
 * DATE - Date value starting from today, incrementing by 1 day
 * TIMESTAMP - Same as DATE
 * BLOB - getBytes() from VARCHAR strategy

If you want to configure your own custom AutoGenerateStrategy implementations, this can be done by contributing a AutoGenerateSourceContribution:

```java
import java.sql.Types;
import java.util.Random;
...
final Random random = new Random();
AutoGenerateStrategy randomIntegerStrategy = new AutoGenerateStrategy() {
   public Object generate(PropertyDef propDef, int increment) {
      return random.nextInt();
   }
};
AutoGenerateStrategy fooStringStrategy = new AutoGenerateStrategy() {
   public Object generate(PropertyDef propDef, int increment) {
      return "foo";
   }
};
AutoGenerateSourceContribution contribution = new AutoGenerateSourceContribution()
	.withAutoGenerateStrategy(Types.INTEGER, randomIntegerStrategy)
	.withAutoGenerateStrategy("author", "authorHobby", fooStringStrategy);

Tader tader = new TaderBuilder()
   .withCoreServices()
   .withCoreJdbcServices()
   .withCoreTypeCoercerContributions()
   .withCoreAutoGenerateSourceContributions()
   .withConnectionSource(connectionSource)
   .withServiceInstance(NameTranslator.class, UpperCamelNameTranslator.class)
   .withContribution(AutoGenerateSource.class, contribution)
   .build();
```

#### NameTranslator

TODO

#### TypeCoercer

TODO

#### Examples

Currently, the best example is  [TaderIntegrationTest.java](https://github.com/uklance/tader/blob/master/tader-core/src/test/java/org/tader/TaderIntegrationTest.java).

#### TODO
* Deploy artifacts to a maven repository
* Documentation, documentation, documentation
