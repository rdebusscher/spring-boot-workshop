# Getting Started

This Maven project shows how you can set up a Spring Boot application with REST endpoints.

## Prerequisites

This continues on the 'database' example and does not discuss the basics of setting up a project, defining endpoints, defining Spring Beans as services and dependency injection, or working with a database.  It focuses on improving the testing aspects of your application by defining the data at the start of the test.

If you are not familiar with Spring Boot 3. x, it is a good idea to first look at the Getting Started (directory `rest`) and other examples.


## Running the Application

Compiling and packaging the application into an executable jar file is done using Maven with the command:

    mvn clean package

The application can be run using the `mvn spring-boot:run` command or `java -Dspring.profiles.active=dev -jar target/test-data-1.0.0-SNAPSHOT.jar` after you have packaged the application using Maven.

## Testing

As you already saw, when you executed `mvn clean package`, the unit tests ran.  To run all tests, including the integration tests, execute this Maven command:

    mvn clean verify

## Discussion

This example is in many ways similar to the 'database' example. It adds some functionality for setting up the database at the beginning of the test.

### Run tests

First, let's have a quick look at the changes in the pom.xml file to run all the tests.

There is the addition of the `maven-surefire-plugin`, which is configured to run all the unit tests. We configure the plugin to check for classes where the name ends with `Test`.

The second plugin is the `maven-failsafe-plugin`, responsible for running the integration tests. These tests take a little bit more time, since we are spinning up our application with all the Spring beans.
Important here is the following configuration option to define the classpath

    <classesDirectory>${project.build.outputDirectory}</classesDirectory>

so that our tests with Spring (Boot) are running fine within Maven

### Custom JUnit 5 extension for loading data

One of the important parts in this example is the custom JUnit 5 extension, which can load data into our database used during the unit or integration test.  That way, we can ensure that we have a database in a known state and we can predict the behavior of our code.

Have a look at the `TestDataExtension` class if you want to find out the programming details.

In short, when added to your test class, it looks for the `@ExcelDataSet` annotation on your class or the test method.  If it finds them, it loads the Excel file indicated using the Apache POI dependency, and uses DBUnit to load the data in the database indicated by the DataSource Spring bean, which is available.

In our case, it loads the data into the H2 in-memory database.  And at the end of the test method, it clears the tables where data are loaded at the beginning of the method so that we can start over for the next test method with a clean, empty database.

For the system to work, we have added 2 dependencies to the Maven `pom.xml` file: DBUnit and POI.

We also had to fix an issue we have with Hibernate 6 and DBUnit where the SQL type for enum types doesn't match. See `CustomH2DataTypeFactory`.

With that in place, our Repository test to find out if the repository methods do what we think they are doing can be written as

````
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ExtendWith(TestDataExtension.class)
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    @ExcelDataSet("classpath:/data/CompanyRepository.xlsx")
    void findByName() {
    }
}
````

This custom extension is made because there seems to be no library providing this kind of functionality. There once was 'spring-test-dbunit' but it is no longer maintained.

### Verifying data in the database

Besides importing data into the database at the beginning of the test, we should also be able to verify if the correct records are added, updated or removed by our code.

Since some development teams can handle the principle that within a test, no actual code of your application should be used to verify the contents (since that can contain an error), we can use DBUnit to make that verification.

For example, in the `EmployeeRepositoryTest.deleteAllByCompanyId` test method, we check if the employee records of the deleted company are removed with these assertion statements

        ITable employee = databaseConnectionProvider.get().createTable("employee");
        Assertions.assertThat(employee.getRowCount()).isEqualTo(1);
        Assertions.assertThat(employee.getValue(0, "company_id")).isEqualTo(BigInteger.valueOf(-2L));

Where the `databaseConnectionProvider` is defined as

    @Autowired
    protected Provider<IDatabaseConnection> databaseConnectionProvider;

It is the DBUnit connection to the database that is made available to the Spring Context of our test by the `TestDataExtension`.

This works fine, but is 'rather sql-alike' if you see how we check for a certain value;  get the value of 'company_id' field in the first row:

    employee.getValue(0, "company_id")

As a rather experimental kind of thing, I have created a custom mapping tool that uses the information for the Hibernate metamodel, which is determined at startup to map the database table back to entity instances.

The same test could now be written as

        List<Employee> employees = getRowsFor("employee", Employee.class);
        Assertions.assertThat(employees).extracting(e -> e.getCompany().getId()).containsExactly(-2L);

Much more Java-alike. The code I included does not handle all types of mapping, but it should give you an idea.

But I believe we can just use the Repository classes to make our assertions.  Look for that version of this example in the directory 'test-data-simple'.

### Calling endpoints

Already present in the 'database' example, there is an enhanced version of the class `AbstractEndpointTest` included, which calls the endpoints in a separate thread.  This makes sure there is no interaction between the Hibernate sessions of the application and the test.
This resembles the production case better, where each call has its own context.

Also, have a look at the class `BusinessValidationMvcResultCheck`, which checks if the expected `ProblemDetail` is returned in the response body in case there was a `BusinessValidationException`.

### Validation

This brings us to the other topic, which is still missing besides security: the validation of the payload.

We have already implemented some business logic in the class `CompanyValidationService`. We introduced this concept in the 'business-logic' example. But we are still missing some validations, like how to make sure that a company name is provided and never contains only blanks.

That is checked by the test method `CompanyControllerIT.createCompany_validationIssue`.  This fails for the moment since we did not implement this, but it will be handled in detail by the example in the 'validation' directory.


## Next Steps

- Use Repository call as the assertions in your test method (see 'test-data-simple')
- Use validation annotations for simple payload checks. (see 'validation')