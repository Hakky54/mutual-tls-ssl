# Example server configuration with Sun Http(s)Server

## Introduction
The main tutorial demonstrates various http clients with a spring-boot server. However not everyone is using spring-boot and therefore it is also good to have examples for other servers. 

## Getting started
The server is using a shared library for fetching the keystores during startup time. You can either use the main [Readme](../README.MD) or the available scripts within the [script directory](../script) to create the required keystores. 

Run one of these commands to run the scripts:
* One way authentication: `./configure-one-way-authentication`
* Two way authentication: `./configure-two-way-authentication-by-trusting-each-other my-company-name`
* Two way authentication by trusting the Certificate Authority: `./configure-two-way-authentication-by-trusting-root-ca my-company-name`

### Starting the server
1. Initially run `mvn clean install` from the root directory
2. Either start the server with `mvn exec:java`, `java -jar target/server.jar` or by running the main method within your IDE from the [App class](src/main/java/nl/altindag/server/App.java)

### Loading the correct application properties
The server has three predefined application properties which can be used straight away for the following use cases:
* No security -> `application-without-authentication.properties`
* One way authentication -> `application-one-way-authentication.properties`
* Two way authentication -> `application-two-way-authentication.properties`

These properties can be injected by using for example the following commands:
```
java -Dproperties="application-one-way-authentication.properties" -jar target/server.jar
```
or by running the main method from your IDE while adjusting the location of the properties.

### Reloading keystores into executable jar
Make sure to rebuild the project if you're using the jar to run the server and if you plan to adjusting the keystores within the shared server resources.
Below is an example command to build with the minimum required maven commands:
```
mvn -pl shared-server-resources,server-with-sun-httpserver install
```
