# Getting Started

This Maven project shows how you can set up a Spring Boot application with REST endpoints.

## Prerequisites

This continues on the 'test-data' example and provides an easier alternative for verifying the data within the database at the
beginning and end of the test.  It does not discuss the testing and database aspects anymore.
Look at the 'test-data' and 'database' directories to learn more about them.

If you are not familiar with Spring Boot 3.x, it is a good idea to first look at the Getting Started (directory `rest`) and other examples.


## Running the Application

Compiling and packaging the application into an executable jar file is done using Maven with the command

    mvn clean package

The application can be run using the `mvn spring-boot:run` command or `java -Dspring.profiles.active=dev -jar target/test-data-simple-1.0.0-SNAPSHOT.jar` after you have packaged the application using Maven.

## Testing

To run all tests, including the integration tests, execute this Maven command

    mvn clean verify

## Discussion

In this version of the 'test-data' example, I have removed the registration of the `IDatabasConnection` from DBUnit as a Spring Bean
or as a bean that you can use in the Repository tests (there is no longer the `TestDataHelper` parent class).
And also the custom mapping code, which converts the data retrieved by DBUnit to Java instances, is removed.

### Data retrieval

Instead, you should just use the Repository classes.  Most of the time, you will use the `findAll` and `findById` methods.
These are methods provided by the Spring framework, so we can assume they perform the task without any issue. And
you can even use some of the custom methods in the repositories that you added.  You have tests for them, so when they succeed, you can assume they work properly and can be used within other tests without compromising the reliability
of the test.

### Validation

The validation topic, which was mentioned at the end of the discussion in the 'test-data' example, is still to be solved.

`CompanyControllerIT.createCompany_validationIssue` still fails and will be solved in the 'validation' directory.


## Next Steps

- Use validation annotations for simple payload checks. (see 'validation')
- Always use the actual database instead of the in-memory database for testing (see 'testcontainers')