# UI Test Automation Project

This project contains UI automation tests utilizing Selenium WebDriver, JUnit 5, and Maven. It supports running tests both locally and in a Dockerized environment with Selenium Grid.

## Prerequisites

- **Java JDK 21**: The project uses JDK 21 (as configured in the Dockerfile). Ensure you have it installed locally for best compatibility.
- **Maven**: For dependency management and running knowledge.
- **Docker & Docker Compose**: For running tests in containers.
- **Chrome/Firefox Browsers**: For local test execution.

## Installation

1.  Clone the repository.
2.  Navigate to the project directory:
    ```bash
    cd ui/UiTestAutomation
    ```
3.  Install dependencies:
    ```bash
    mvn clean install -DskipTests
    ```

## Running Tests Locally

You can run tests using Maven. By default, it runs on **Chrome** in normal (headed) mode.

### Default Execution
```bash
mvn clean test
```

### Customizing Execution
You can pass system properties to customize the browser, execution mode, and headless state.

**Options:**
- `-Dbrowser`: `chrome` (default) or `firefox`
- `-Dheadless`: `true` or `false` (default)
- `-Dexecution_mode`: `local` (default) or `grid`

**Examples:**

Run on **Firefox**:
```bash
mvn clean test -Dbrowser=firefox
```

Run **Headless** (no UI visible):
```bash
mvn clean test -Dheadless=true
```

Run on Chrome with **Headless** mode:
```bash
mvn clean test -Dbrowser=chrome -Dheadless=true
```

## Running Tests in Docker

The project includes a `docker-compose.yml` file to run tests in an isolated Docker environment with Selenium Grid (Chrome & Firefox nodes).

### Start and Run Tests
To build the test container and run the tests:

```bash
docker-compose up --build tests
```

This command will:
1.  Spin up a Selenium Hub.
2.  Spin up Chrome and Firefox Nodes.
3.  Build the project `Dockerfile`.
4.  Run the tests inside the container using the configuration defined in `docker-compose.yml`.

### Docker Configuration
You can configure the test run by modifying the environment variables in the `tests` service within `docker-compose.yml`:

```yaml
  tests:
    environment:
      - execution_mode=grid
      - grid_url=http://selenium:4444
      - browser=chrome     # Change to 'firefox' to run on Firefox
      - headless=true      # Change to 'false' for headed mode (not recommended for Docker)
```

## Viewing Reports
The project generates Allure reports. verify the `target/allure-results` directory for output.
