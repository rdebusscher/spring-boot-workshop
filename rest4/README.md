# Getting Started

This Maven project shows how you can setup a Spring Boot application with REST endpoints.

## Prerequisites

Since we are using Spring Boot 4.x, you need at a minimum Java 17 to compile and run the application.

## Running the Application

Compiling and packaging the application into an executable jar file is done using Maven with the command

    mvn clean package

The application can be run using the `mvn spring-boot:run` command or `java -jar target/rest-1.0.0-SNAPSHOT.jar` after you have packaged the application using Maven.

## Calling the endpoints.

There are 2 endpoints in the application that can be accessed using the `curl` command:

    curl localhost:8080/hello
    curl localhost:8080/person
 
## Discussion

See the comments in the `pom.xml` file and source code to learn more.

## Next Steps
