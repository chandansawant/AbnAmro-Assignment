# Getting Started

## Architecture
- Application follows a layered design.
  - Web layer provides REST end-points
  - Service layer contains business logic
  - Repository layer handles database interaction
- Search operation is supported using POST method (instead of widely used GET)
  - Larger set of criteria values can be easily supported
- Few custom exceptions defined with two types.
  - API related exceptions
  - Application logic related exceptions

## Assumptions
1. REST Api
   - Authentication and authorization is not needed for this task
   - API versioning is not in the scope of the task.
   - PATCH requests are not required.
   - Additional API features are not required.
     - e.g. rate limiting, pagination, sorting, etc.
2. Data validation is done using basic checks.
3. Persistent database
   - In memory H2 database based persistence is used for the assignment.

## How to run application
- It's a stand-alone Java application.
  - `com.challenge.abnamro.RecipeManagerApplication` contains `main()` method
- Spring framework provides in-build web server to enable REST API end-points.

## Useful links
When application is running, additional information will be available on following URLs.
- Open API based api-docs - <http://host:port/applicationContext/v3/api-docs>
   - e.g. <http://localhost:8080/recipesApp/v3/api-docs>
- Swagger-ui - <http://host:port/applicationContext/swagger-ui.html>
   - e.g. <http://localhost:8080/recipesApp/swagger-ui.html>
- Actuators - <http://host:port/applicationContext/actuator/health>
   - e.g. <http://localhost:8080/recipesApp/actuator/health>

## Technical Details
- Java 8
- Spring Boot is used as DI framework.
- REST API a.k.a. web layer
  - Expose API endpoints
  - Validates input data
  - CRUD + Search operations are served by separate end-points along with unique Http method 
- Service layer contains business logic
- JPA repository handles interaction with database
  - All changes to database are done within transactions
  - Transactions are handled by Spring framework
- Data transfer across layers is handled by various DTOs
  - DTOs avoid leaking JPA managed entities getting exposed
- H2 Database is select as persistence storage
  - Easily integrates with Spring Boot framework
- Unit and integration tests are prepared using Spring Boot + JUnit
- Maven is used as build tool.
