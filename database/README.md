# Getting Started

This Maven project shows how you can set up a Spring Boot application with REST endpoints.

## Prerequisites

This continues on the Getting Started and Business Logic example and does not discuss the basics of setting up a project, defining endpoints, or defining Spring Beans as services and dependency injection.

If you are not familiar with Spring Boot 3. x, it is a good idea to first look at the Getting Started (directory `rest`).

This example also requires a Docker environment that runs the database. See the next section.

## Setting up database

The current setup of the application is to have a MySQL database, which is accessible on port 3306 on your local development machine. This can be achieved by executing the following command from within the `src/main/docker` directory.

     docker compose up -d

It assumes you have Docker Desktop installed on your machine and is up and running. You should see the following message when the database is running.

     Container MySQL started  

See also the discussion about the database configuration if you want to connect to another database instance during development.

## Running the Application

Compiling and packaging the application into an executable JAR file is done using Maven with the command.

    mvn clean package

The application can be run using the `mvn spring-boot:run` command or `java -Dspring-boot.run.profiles=dev -jar target/database-1.0.0-SNAPSHOT.jar` after you have packaged the application using Maven.

## Calling the endpoints.

There are several endpoints in the application that can be accessed. You can find them in the `CompanyController` and `EmployeeController` classes.  The `commands.txt` file contains some 'curl' example commands to call these endpoints.


## Discussion

### Configuration

In order to be able to access the MySQL database, in our case, we need a few additional dependencies in the `pom.xml` file.

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
            <version>${spring.boot.version}</version>
        </dependency>

        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>9.3.0</version>
            <scope>runtime</scope>
        </dependency>

The `spring-boot-starter-data-jpa` dependency is a dependency that provides the JPA (Java Persistence API) support for Spring Boot. It adds Hibernate as the JPA provider and HikariCP to handle the connection pool for database connections.

The `mysql-connector-j` dependency is the MySQL connector for Java. It is required at runtime to connect via JDBC to the MySQL database.

The 'data jpa' starter contains all the required configuration, like transaction management and connection pooling, for our application. We only need to provide the connection information for the database in our application configuration (the `application.properties` file)

Since we like to use a different database during development than in production (or in general, a different database depending on the environment), we make use of the Spring profiles to define the database connection properties for each environment.  See the next section.

There is one setting we should change, the OpenInView setting, which should be false (true by default)

````
spring.jpa.open-in-view=false
````

The OpenInView property defines the scope of the hibernate session and when a lazy-loaded collection should still be retrieved from the database. This should be aligned with the transaction boundaries, which we will place on the service layer, and thus the property should be false so that they are aligned (and we avoid some potential performance issues).

It is the task of the service layer to load the data that is required so that it can avoid inefficient N+1 query situations.

### Profiles

As mentioned, we need different database connection values for each environment. Spring framework, and thus usable in Spring Boot, has a concept of profiles for this.

You specify the values in different properties files with a suffix of the profile name, and during startup of the application, you indicate the profile the application needs to use.

That way, you have the correct settings per environment.

Within the source resource folder, you can find the files.

````
application-dev.properties
application-prod.properties
````

The `-dev` one contains the values to link to the MySQL server we have started in our Docker engine. (which is localhost).  The production file is not valid, just an indication of what the file could look like, since that should contain the values of the production environment, which we don't have in our case.

If you make a mistake in the configuration of the database connection parameters or the profile setup, you see a message similar to this one.

````
Failed to configure a DataSource: 'url' attribute is not specified, and no embedded datasource could be configured.

Reason: Failed to determine a suitable driver class
````


### Repository

The repository is the component that is responsible for interacting with your database through JDBC (Java Database Connectivity). It reads, inserts, updates, and deletes records.

Some generic operations, like read all records from a table, read a specific record based on its id column value, can be done generically and can be found in the `org.springframework.data.jpa.repository.JpaRepository` interface.  Other, specific functionality for your application, like reading all employees of a specific company, needs to be implemented by the developer.

Here is an example of how you could realize this

````
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE e.company.id = :companyId")
    List<Employee> findAllByCompanyId(@Param("companyId") Long companyId);
````

You see it correctly, you just define an interface with the SQL query that needs to be executed when the method is called. At runtime, Spring Boot will create the implementation for the interface, which will run that Hibernate Query (so it is a query on Java objects, not the database, and Hibernate uses the Entity mapping in your project to make the mapping).

Although Spring Data can create queries based on the method name (like 'findByLastName' or 'findByAgeGreaterThan'), it is not recommended, as that can result in complex method names, and you will also have some cases with some more complex queries that you can't express this way.

So `@Query` is recommended all the time for easier readability.

### Testing Repositories

Since you can make mistakes in the Hibernate queries, you should test out your Repository methods.  This will be an integration test, since you need more than just your Java code; you need a database. But due to the fast setup of the test environment (Spring context with only Repository beans and an in-memory database), we can run it with JUnit as a 'unit test.'

You could use a specific MySQL database instance for this, but since each test should have a known initial state of the database before the test starts, it is better to use an in-memory database, like the H2 database, which Spring Boot supports by default, for this, which can be created and destroyed each time.

Using H2 means that your Repository should make use of Hibernate queries and not native queries (which is also supported by the `@Query` annotation). Since those native queries will probably not work on H2, no tests can be written for them.

There is a solution in case you really need a native query. Have a look at ??? for an example.

Spring Boot has a specific test annotation for testing those queries, `@DataJpaTest`. Have a look at the classes `CompanyRepositoryTest` and `EmployeeRepositoryTest`.

The initial state of the database is created by calling the entity manager with test records in a `@BeforeEach` annotated method. This is not the most ideal way of loading test data; see the example in the 'test-data' directory.

## Entities

It was already mentioned that the entity classes define the mapping between the database tables and fields and the Java objects and their properties.

Also, here an explicit indication of information is recommended, so it is best to annotate properties with `@Column`, although it isn't strictly needed.

The additional information defined within the `@Column` annotation, like length, required, unique, etc, is used when Hibernate generates the database structure in the H2 instance during the tests.

See also some other important remarks about the technical and functional keys within the entity classes `Company` and `Employee`.

## Database structure evolution

It is very common that after your application is in production, you need to adapt your database structure (additional table, additional column, etc).

It is important to never let Hibernate update the database structure according to the state of your entities. This could be done in theory by setting the correct configuration value for the property 'spring.jpa.hibernate.ddl-auto', but it is very dangerous.

So see the configuration of the property in the production profile. When running locally (the dev profile), it is not a real problem to have the structure update automatically.  But not needed locally when using tools like Liquibase and Flyway as described later in this section.

It would not be the first time that production data is lost or corrupted using this auto update functionality. So always maintain the structure 'manually' using scripts.  These scripts can be applied together with the update of your application in production (or each environment you propagate the new version to).

Liquibase and Flyway tools can also be used as the 'manual' way to change the database structure. The main advantage of these two tools is that they allow you to combine the required changes in your source code repository, and the correct scripts are executed when needed.

## DTOs and mappers

In many cases, you don't need the entire entity classes in your view. Most of the time, you have a subset of data or data in another structure that is sent to and from your REST endpoints.  Therefore, you probably have DTO classes passed from the rest endpoints to the service methods and vice versa.

So the service layer needs a way to convert the entities to and from DTO classes.  This is the responsibility of the Mapper service. These classes can be defined as Spring Component beans.

Alternatively, you can use libraries like [MapStruct](https://mapstruct.org/) to generate those mapping methods automatically. You define the mapping methods in an interface, and with the correct annotations, you can indicate special mappings (when mapping can't be performed based on the name of the properties). But to avoid compiler warnings, you should indicate those properties that are ignored. And sometimes the mapping isn't straightforward because of complex manipulation, and you still need a Java-based method. So libraries as MapStruct should be carefully selected.

## Integration testing endpoints

Just as we had in the example project in the `business-logic` directory, you can create integration tests that call the endpoints and use all components of your application, including the Repositories we have introduced here.

As an example, you can look at the `CompanyControllerIT` class.  You see that it is almost identical to the structure of the test we had in the `business-logic` project.  `@SpringBootTest`, `@AutoConfigureMockMvc`, and `AbstractEndpointTest` (our parent helper classes for calling the endpoints) are also present here.

The difference is that we use the `EntityManager` to prepare the database at the start of the test. Because we use an entity manager, we also need to declare the test methods transactional.

But if you run the test, you will see that the test method `deleteCompany` fails.  This is due to the interaction of the transaction of our test method and the transaction for pur endpoints.

And you can work around this problem by wrapping the call to the endpoint in a separate thread or wrapping the access to the entity manager in our tests in a separate thread and using Spring's `TransactionTemplate`.
Or you can use the `WebTestClient` to call the endpoints of a 'real application instance' you have started. But that requires more work to have a database in a known state at the beginning of the test.

This problem will also be solved in the `test-data` example project.

## Next Steps

- Load data the easy way into your database at the start of your test method. (see 'test-data')
- Use Repository calls as the assertions in your test method (see 'test-data-simple')
- Use validation annotations for simple payload checks. (see 'validation')