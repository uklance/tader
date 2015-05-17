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

At the moment the project documentation is poor. See [GraterImplTest.java](https://github.com/uklance/grater/blob/master/grater-core/src/test/java/org/grater/GraterImplTest.java) for usage.
