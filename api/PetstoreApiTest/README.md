# Petstore API Test Automation

This project contains API automation tests for the [Petstore Swagger API](https://petstore.swagger.io/), written in **Kotlin** using **Rest Assured** and **JUnit 5**.

## 🛠 Prerequisites

*   **Java JDK 17+** (Project uses Kotlin with JVM target 1.8, Docker image uses Java 21)
*   **Maven** 3.8+
*   **Docker** & **Docker Compose** (for containerized execution)

## 🚀 Setup

1.  Clone the repository:
    ```bash
    git clone <repository-url>
    cd PetstoreApiTest
    ```

2.  Install dependencies:
    ```bash
    mvn clean install -DskipTests
    ```

## 🧪 Running Tests Locally

To run the tests on your local machine using Maven:

```bash
mvn clean test
```

This will run all tests using the configuration specified in `pom.xml`.
Tests are configured to run in parallel by default.

## 🐳 Running Tests on Docker

You can run the tests within a Docker container to ensure a consistent environment.

### Using Docker Compose (Recommended)

Run the tests using Docker Compose:

```bash
docker-compose up --build tests
```

This command will:
1.  Build the Docker image containing the test project.
2.  Run the tests inside the container.
3.  Save the Allure test results to `./target/allure-results` on your host machine.

## 📊 Test Reporting

This project uses **Allure Report** for visualizing test results.

After running the tests (locally or via Docker), you can view the report by running the provided script (assuming a local Allure setup or the embedded binary path acts correctly):

```bash
./show-allure-results.sh
```

*Note: The script assumes a local Allure binary at `.allure/allure-2.36.0/bin/allure`. If you have Allure installed globally, you can simply run:*
```bash
allure serve target/allure-results
```

### ⚙️ Allure Maven configuration

- **Results directory**  
  - Configured via `maven-surefire-plugin` system property:  
    `allure.results.directory = ${project.build.directory}/allure-results`  
  - Effective path at runtime: **`target/allure-results`** (also mounted out of the Docker container).

- **Report generation plugin**  
  - Maven plugin: `io.qameta.allure:allure-maven`  
  - **`resultsDirectory`**: points to `${project.build.directory}/allure-results`  
  - **`reportDirectory`**: points to `${project.build.directory}/allure-report` (HTML output).

- **Allure CLI download URL**  
  - The plugin is configured with an `allureDownloadUrl` that tells Maven where to download the Allure Commandline ZIP.  
  - You can change this URL (e.g. to an internal Nexus/Artifactory) without changing test code; only the `pom.xml` needs to be updated.

- **Useful Maven goals**  
  - Generate report after tests:  
    ```bash
    mvn allure:report
    ```  
  - Generate and serve report locally (temporary directory):  
    ```bash
    mvn allure:serve
    ```
