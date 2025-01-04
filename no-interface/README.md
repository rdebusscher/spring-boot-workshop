# No interfaces fo services

This Maven project shows that you do not need to create interfaces for the services (like the business logic and validation services) to have all the functionality.

## Prerequisites

This project is a rework of the Exception handling one (directory `exception-handling`). So it might be good to look at that project to understand all its details.

## Running the Application

Compiling and packaging the application into an executable jar file is done using Maven with the command:

    mvn clean package -DskipTests

The application can be run using the `mvn spring-boot:run` command or `java -jar target/rest-1.0.0-SNAPSHOT.jar` after you have packaged the application using Maven.

Remark: Tests are fully functional but if you just want to run the application, there is no need to run them, and thus the option
`-DskipTests` can be used.

## Calling the endpoints.

The following call of the endpoint will result in a `400 Bad Request` response.

    curl -X POST --location "http://localhost:8080/loan" -H "Content-Type: application/json" -d '{
      "amount":-100000.0,
      "years":10
     }'

## Discussion

Many developers use an interface and an implementation class for the Spring Service classes.  This pattern was also used in the Business logic and Exception-handling demos.

However, this is not according to the Java principle that an interface should describe the functionality of a set of different implementations. Having an interface should imply that you have multiple classes implementing this interface.

But this is usually not the case in your Spring Service classes.  You have a single class implementing the interface and that class is the only one that should be used. So why the interface?  In the past, this was more or less needed to allow the application to function as there is a need for interceptors. But nowadays, this is no longer the case and the _contract_ of your service are the public methods. And all functionality, from interceptors and creation of mocks in test can be done on actual classes without an interface.

So in this project, the code of the Eception handling project is adjusted and the interface for the Service and validation classes are removed.
And you still have the same functionality within your application and tests.

## Next Steps

- Access a relational database (see '`database`)