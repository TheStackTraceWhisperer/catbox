# Coverage Report Module

This module aggregates test coverage reports from all RouteBox modules.

## Purpose

This POM-only module exists solely to generate an aggregated JaCoCo coverage report across all modules in the RouteBox project:
- routebox-common
- routebox-client
- routebox-server
- order-service

## Generating Coverage Reports

### Local Development

To generate coverage reports locally:

```bash
# Run tests and generate coverage
mvn clean verify

# View aggregate report
open coverage-report/target/site/jacoco-aggregate/index.html
```

### Individual Module Reports

Each module also generates its own coverage report:

```bash
# View individual module reports
open order-service/target/jacoco-ut/index.html
open routebox-server/target/jacoco-ut/index.html
```

## Report Locations

After running `mvn verify`, coverage reports are available at:

- **Aggregate Report**: `coverage-report/target/site/jacoco-aggregate/index.html`
- **Unit Test Reports**: `<module>/target/jacoco-ut/index.html`
- **Integration Test Reports**: `<module>/target/jacoco-it/index.html` (if integration tests exist)

## CI/CD Integration

The GitHub Actions workflow automatically:
1. Runs all tests
2. Generates coverage reports
3. Uploads reports as build artifacts

Coverage reports can be downloaded from the GitHub Actions run page under "Artifacts".

## Understanding Coverage Metrics

JaCoCo provides several coverage metrics:

- **Instructions**: Java bytecode instructions
- **Branches**: if/else branches in the code
- **Lines**: Source code lines
- **Methods**: Methods and constructors
- **Classes**: Classes and interfaces

A high coverage percentage doesn't guarantee bug-free code, but it helps identify untested areas.
