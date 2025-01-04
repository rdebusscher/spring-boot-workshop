# Using Undertow

This Maven project shows how you can _Undertow_ as the web server for a Spring Boot application. It is a high-performance, lightweight alternative for the default Apache Tomcat. It can handle both blocking and non-blocking IO, which makes it more flexible for different types of workloads.

## Prerequisites

This continues on the Getting started example but can be applied to your project at any point.

So it is good to have a look at the Getting started (directory `rest`) first if you are not familiar with Spring Boot 3.x.
 
## Discussion

The change is configured by excluding the Apache Tomcat from the _starter web_ package and adding a dependency for Undertow.

Also have a look at the `UndertowConfiguration` class about a warning of using a default configuration that you can find in the log. 

## Next Steps

- Implementing business logic. (see `busines-logic`)