# Copilot Instructions for RouteBox Project

## Technology Requirements

### Java Version
- **MUST use Java 21** for all development
- Virtual threads are a core feature of this project and require Java 21+
- Do not downgrade to earlier Java versions

### Virtual Threads
- The application uses Java 21 virtual threads for concurrent processing
- Virtual threads are used for:
  - Web request handling (Tomcat protocol handler)
  - Async task execution (Spring's async task executor)
  - Event processing (each outbox event is processed in its own virtual thread)
- When modifying code that processes events or handles async operations, use `Thread.ofVirtual().start()` instead of traditional thread pools

## Code Style
- Follow existing code patterns in the repository
- Use Lombok annotations where appropriate
- Maintain consistent logging patterns

## Testing
- All changes must pass existing tests
- Add tests for new functionality
- Use JUnit 5, AssertJ, and Spring Boot Test framework

## Build Requirements
- Maven 3.6+ is the build tool
- The project uses Spring Boot 3.5.7
- Run `mvn clean test` to verify changes

## Documentation Guidelines
- Documentation should describe the **current state** of the system only
- Do NOT add progress tracking, change logs, or diff details to documentation
- Do NOT create documents that track issues found and fixed
- Do NOT add review documents, analysis documents, or historical tracking
- Code changes and fixes should be described in commit messages and PR descriptions, not in documentation files
- If you need to track progress or changes during development, use the PR description or commit messages
- Documentation files should be reference material for users, not a log of development activities
