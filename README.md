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

#### Example
At the moment the project documentation is lacking, for sample usage see [TaderIntegrationTest.java](https://github.com/uklance/tader/blob/master/tader-core/src/test/java/org/tader/TaderIntegrationTest.java).


#### TODO
* Deploy artifacts to a maven repository
* Documentation, documentation, documentation
