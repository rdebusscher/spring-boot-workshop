# Writing business logic

This project shows how you should write the business logic related to the processing of an endpoint.

## Prerequisites

This continues on the Getting Started example and does not discuss the basics of setting up a project or defining endpoints.

If you are not familiar with Spring Boot 3. x, it is a good idea to first look at the Getting Started (directory `rest`).

## Running the Application

Compiling and packaging the application into an executable jar file is done using Maven with the command:

    mvn clean package

After you have packaged the application using Maven, you can run it using the `mvn spring-boot:run` command or `java—jar target/business-logic-1.0.0-SNAPSHOT.jar`.

## Calling the endpoints.

There is 1 endpoint in the application that can be accessed using the `curl` command:

    curl -X POST --location "http://localhost:8080/loan" -H "Content-Type: application/json" -d '{
          "amount":100000.0,
          "years":10
        }'

For more examples, see the `commands.txt` file.

## Discussion

There are 2 main aspects of application development shown in this example. The business logic implementation and handling of the validation of the parameters that are provided by the user.

The business logic should be located in 'services' classes (there are alternative names) which should be independent of the view layer (the rest endpoints in our case) and independent of the data storage.
The logic should start by validating the parameters received by the end user. Also in case a front-end already performed the validations since there is no guarantee that the endpoint is called only from the front-end.

If the validation has different parts, in our case validation of the values for the amount and the years, they should be done in separate methods (see class `LoanCalculatorValidationServiceImpl.class`) and within the Unit Tests, we can verify if the different parts are called and if each part does what it should do.
If you don't have testing support in your project, make sure you add the `spring-boot-starter-test` artifact.

For an example of such a test that makes use of `Mockito`, have a look at the class `LoanCalculatorValidationServiceTest`.

Another type of test involves integration tests where the application is tested under circumstances very similar to the production usage. So we start up the Spring Boot application and call the endpoint and verify the result. An initial attempt can be seen in the class `LoanControllerIT` but will be more functional and better when we have implemented correct exception handling and introduced database access.

In the example, the services are defined using an interface. Many Spring (Boot) users do it like this although this is not strictly needed. Also according to the Java principles of an interface, you should not define an interface if you have just one implementation.  There is a version of this project that does not make use of interfaces but has the same functionality and ease of implementation.

## Next Steps

- Version of this example where we do not make use of interfaces.  (see `no-interface`)
- Proper handling of exceptions and using the 'Problem Details for HTTP APIs' specification. (see `exception-handling`)