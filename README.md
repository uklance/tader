![Tader](https://github.com/uklance/tader/raw/master/tader_250.png)

Tader Test Data
---------------

Tader helps with generating test data prior to testing DAO's, stored procedures or any other service that requires
relational data. 

#### How It Works
Tader has knowledge of your relational database model including primary keys, foreign keys and required columns.
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
   "jdbc:h2:mem:%s;DB_CLOSE_DELAY=-1",
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

#### TODO
* Deploy artifacts to a maven repository
* Documentation, documentation, documentation
