# Disable serving static content

In this project, we configure Spring to not serve static content.

## Prerequisites

This continues on the Getting Started example but can be applied to your project at any point.

So it is good to have a look at the Getting started (directory `rest`) first if you are not familiar with Spring Boot 3.x.

## Running the Application

Compiling and packaging the application into an executable jar file is done using Maven with the command:

    mvn clean package

After you have packaged the application using Maven, you can run it using the `mvn spring-boot:run` command or `java—jar target/rest-1.0.0-SNAPSHOT.jar`.

## Calling the content

Within your browser, navigate to

    http://localhost:8080/about.xhtml

This should result in a page saying there was an error locating the resource. Unless you place the configuration in the application.properties file in comments.

The endpoints still function as expected.

    curl localhost:8080/hello

## Discussion

Within an application with REST API endpoints, you normally don't need to serve some static content. Files like html, css or javascript files are normally served to your browser from another application. So disabling this option is a good security practice so that you don't expose by accident some sensitive content.