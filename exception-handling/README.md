# Exception handling

This Maven project shows how you can setup a generic Exception handling mechanism that follows the problem+json standard.

## Prerequisites

This continues on the Business Logic and Validation of parameters example. So it might be interesting to have a look at that example (directory `business-logic`) first.


## Running the Application

Compiling and packaging the application into an executable jar file is done using Maven with the command

    mvn clean package -DskipTests

The application can be run using the `mvn spring-boot:run` command or `java -jar target/rest-1.0.0-SNAPSHOT.jar` after you have packaged the application using Maven.

Remark: Tests are fully functional but if you just want to run the application, there is no need to run them and thus the option 
`-DskipTests` can be used. 

## Calling the endpoints.

The following call of the endpoint will result in a `400 Bad Request` response. 

    curl -X POST --location "http://localhost:8080/loan" -H "Content-Type: application/json" -d '{
      "amount":-100000.0,
      "years":10
     }'
 
## Discussion

An easy and generic exception handling mechanism makes returning a consistent and correct response simple. The RFC 7807 - Problem Details for HTTP APIs (https://tools.ietf.org/html/rfc7807) is a standard for representing errors in HTTP APIs. and is supported by the Spring Boot framework.

You can easily activate this and add a simple method to handle your custom exception when you detect a problem in the business logic.

First of all, we create a new class that extends from `ResponseEntityExceptionHandler` and annotate it with the `@ControllerAdvice`.  This will activate the Problem Details for HTTP APIs support and the exception handling mechanism.

In the previous example, we have created the `BusinessValidationException` class that extends from `RuntimeException` and can be used to stop the processing of the request in the case we detect a business logic problem.  (see `busines-logic`)

In this version, we define a constant for each problem, see `DomainErrorCodes`, and add a new method to handle the exception within the `ControllerAdvice` annotated class. By annotating the method with `@ExceptionHandler(BusinessValidationException.class)` we tell Spring Boot to handle the exception. And convert it to a `ProblemDetail` instance which represents the body of the response we return to the caller.

Besides the code in the response which is handy for the other party that called the endpoint to know what was the problem, we can also include a short description of the problem. For that purpose, we define a resource bundle (see `src/main/resources/i18n/validation/messages_en.properties`) that contains the description of the problem. To read it, we define a _MessageSource_ bean that takes the properties file. The definition in this example is made in the `ValidationResourceBundleConfig` class. And it is used in the `ControllerAdvice` class to retrieve the message.


The testing is also extended in this example for the newly added functionality.  First, there is a new performPost (and corresponding performGet method) added to the AbstractEndpointTest that takes `MvcResultChecker` instances to check the response. 
Here you can make use of The Jackson Object Mapper to convert the payload first to an Java instance and then compare it with the expected result. Instead of the Spring Boot supported way of using JsonPath which is using a kind of expression language which is not typesafe and can be more difficult to write.

These methods are used in the `GlobalExceptionHandlerIT` test to verify every detail of the returned JSON response. You can also use it in the verification of endpoint calls when there should be a business logic error. ut it should be enough to test if the response has the correct status as the exception handling is generic and handled correctly whenever yu throw a `BusinessValidationException`.


## Next Steps

- Access a relational database (see '`database`)