# RouteBox ArchUnit Module

This module contains architectural tests for the routebox project using [ArchUnit](https://www.archunit.org/). These tests verify that the codebase follows established architectural principles and coding standards.

## Purpose

The routebox-archunit module enforces architectural rules across the entire routebox project, ensuring:
- Consistent layering and separation of concerns
- Proper naming conventions
- Correct usage of Spring annotations
- Clean package dependencies between modules
- JPA entity and repository best practices
- Appropriate transaction boundaries

## Test Suites

### 1. LayeringArchitectureTest
Tests the layered architecture pattern across the application:
- Controllers should not access repositories directly
- Services should not access controllers
- Repositories should not access services or controllers

### 2. NamingConventionTest
Verifies that classes follow consistent naming patterns:
- Controllers end with `Controller`
- Services end with `Service`, `Handler`, `Publisher`, `Poller`, or `Claimer`
- Repositories end with `Repository`
- Configuration classes end with `Config`, `Configuration`, or `Factory`
- Entities are in entity packages
- Exceptions end with `Exception`
- DTOs end with `Request`, `Response`, `DTO`, or `Dto`

### 3. SpringAnnotationTest
Ensures proper usage of Spring annotations:
- Controllers are annotated with `@RestController` or `@Controller`
- Services are annotated with `@Service`
- Repositories extend Spring Data repository interfaces
- Configuration classes are annotated with `@Configuration`
- Entities are annotated with `@Entity`

### 4. PackageDependencyTest
Enforces clean package dependencies between modules:
- routebox-common should not depend on other modules (it's the foundation)
- routebox-client should not depend on routebox-server or order-service
- routebox-server should not depend on order-service
- order-service should not depend on routebox-server
- Controllers should not depend on other controllers
- Entities should not depend on services or controllers
- Repositories should only depend on entities and Spring Data

### 5. EntityRepositoryPatternTest
Validates JPA entity and repository patterns:
- Entity id fields are annotated with `@Id`
- Repositories are interfaces (Spring Data pattern)
- Entity fields are not public (encapsulation)
- Entities reside in entity packages
- Entities use `@Table` annotation to define table names

### 6. TransactionBoundaryTest
Ensures proper transaction management:
- Service methods that modify data should be transactional
- Controllers should not be transactional (transactions belong in the service layer)
- Repositories should not define their own transactions (Spring Data handles this)
- Service classes should be annotated with `@Transactional` at class or method level

## Running the Tests

```bash
# Run only ArchUnit tests
mvn test -pl routebox-archunit

# Run all tests including ArchUnit
mvn test
```

## Configuration

The module uses an `archunit.properties` file to configure test behavior:
- `archRule.failOnEmptyShould=false` - Allows tests to pass even if no classes match certain patterns, which is useful in a multi-module setup where not all modules have all layer types

## Technology

- **ArchUnit 1.3.0** - Java architecture testing framework
- **JUnit 5** - Testing framework
- **Maven** - Build tool

## Architectural Principles Enforced

Based on analysis of the routebox codebase, these tests enforce:

1. **Layered Architecture**: Clear separation between controller → service → repository → entity layers
2. **Package Organization**: Separate packages for config, controller, service, repository, entity, dto, exception, metrics
3. **Spring Best Practices**: Proper use of Spring annotations and patterns
4. **Transaction Management**: Transactions at the service layer, not in controllers or repositories
5. **JPA Best Practices**: Proper entity design and repository interfaces
6. **Module Independence**: routebox-common is independent; client, server, and order-service are decoupled
7. **Outbox Pattern**: Client layer can access repositories for the transactional outbox pattern

## Benefits

- **Architectural Validation**: Catches violations of architectural principles
- **Documentation**: Serves as documentation of architectural decisions
- **CI/CD Integration**: Can run as part of the build pipeline
- **Team Alignment**: Ensures team members follow the same architectural patterns
- **Refactoring Support**: Provides confidence when refactoring

## Adding New Rules

To add new architectural rules:

1. Create a new test class in `src/test/java/com/example/routebox/archunit/`
2. Use ArchUnit's fluent API to define rules
3. Follow the pattern of existing tests for consistency
4. Consider using `allowEmptyShould(true)` for rules that may not apply to all modules

Example:
```java
@Test
void customRule() {
    ArchRule rule = classes()
        .that().resideInAPackage("..mypackage..")
        .should().doSomething()
        .because("My architectural principle");

    rule.check(importedClasses);
}
```

## Notes

- The module only includes routebox-common and routebox-client as dependencies for testing
- Tests are designed to be flexible for a multi-module architecture where not all modules have all layer types
- The archunit.properties configuration allows tests to pass even when some patterns don't match (e.g., no controller classes in routebox-common)
