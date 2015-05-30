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
   .withConnectionSource(connectionSource);
   .build();
```
#### AutoGenerateSource / AutoGenerateStrategy

TODO

#### NameTranslator

TODO

#### TypeCoercer

TODO

#### Examples

Currently, the best example is  [TaderIntegrationTest.java](https://github.com/uklance/tader/blob/master/tader-core/src/test/java/org/tader/TaderIntegrationTest.java).

#### Maven Usage

```xml
<dependencies>
   <dependency>
      <groupId>org.tader</groupId>
      <artifactId>tader-core</artifactId>
      <version>0.0.x</version> 
   </dependency>
</dependencies>
<repositories>
   <repository>
      <id>lazan-releases</id>
      <url>https://raw.github.com/uklance/releases/master</url>
   </repository>
</repositories>
```

#### TODO
* Deploy artifacts to a maven repository
* Documentation, documentation, documentation
