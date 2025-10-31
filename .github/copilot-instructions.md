# Copilot Instructions for Catbox Project

## Technology Requirements

### Java Version
- **MUST use Java 21** for all development
- Virtual threads are a core feature of this project and require Java 21+
- Do not downgrade to earlier Java versions

### Virtual Threads
- The application leverages Java 21 virtual threads for high-performance concurrent processing
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
