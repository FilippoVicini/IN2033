# Testing Documentation for IN2033 Project

## Introduction

This document outlines the testing setup and procedures for the IN2033 project. The goal is to ensure the delivery of a reliable and high-quality automated software solution for Lancaster's Music Hall.

## Objectives

- Validate that the software meets its functional and non-functional requirements.
- Identify and resolve defects during development.
- Ensure compatibility and integration with other teams' modules.

## Testing Framework and Tools

- **JUnit**: For unit testing Java-based components.
- **Maven**: For managing dependencies and running test goals.
- **Database Testing**: Use MySQL test databases to simulate real-world scenarios.

## Setting Up the Testing Environment

### Prerequisites

Ensure you have the following installed on your system:

1. **Java JDK**: [Download JDK](https://adoptium.net/)
    ```bash
    java -version
    ```
2. **Maven**: [Download Maven](https://maven.apache.org/download.cgi)
    ```bash
    mvn -version
    ```
3. **MySQL Database**:
   - Create a test database for running tests.
   - Use the `.env` file to configure database credentials.
   - Example `.env` file:
     ```
     TEST_ADMIN_USER="admin"
     TEST_ADMIN_PASSWORD="password"
     TEST_DATA_USER="data_user"
     TEST_DATA_PASSWORD="data_password"
     ```

### Installing Dependencies

Run the following command to install project dependencies:
```bash
mvn install
```

## Running Tests

### Unit Tests

Run all unit tests using Maven:
```bash
mvn test
```

### Integration Tests

Integration tests validate the interaction between different modules of the application:
```bash
mvn verify
```

### End-to-End Tests

End-to-end tests can be performed manually based on the user scenarios provided in the project documentation.

## Writing Tests

1. **Unit Tests**:
   - Place unit tests in the `src/test/java` directory.
   - Use JUnit annotations like `@Test` to define test cases.
   - Example:
     ```java
     @Test
     public void testSampleFunction() {
         int result = sampleFunction(2, 3);
         assertEquals(5, result);
     }
     ```

2. **Database Tests**:
   - Use the `DBManager` class for database interactions.
   - Mock database connections where applicable.

3. **GUI Tests**:
   - Use JavaFX testing libraries or manual tests for GUI components.

## Test Reports

Test results will be generated in the `target/surefire-reports` directory by default.

To view a detailed HTML report, use the following Maven plugin:
```bash
mvn surefire-report:report
```

