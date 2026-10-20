# 🚀 Task Management API

A robust RESTful API for task management built with **Kotlin** and the **Ktor Framework**, following **Clean Architecture** principles. Designed with clean code practices, comprehensive testing (Unit & Integration tests), structured JSON logging, and environment-based configuration readiness.

---

## 🛠️ Tech Stack & Tools

* **Core:** Kotlin, Ktor (Netty Server)
* **Architecture:** Clean Architecture (Domain, Data, Presentation Layers)
* **Dependency Injection:** Koin
* **Database & ORM:** SQLite, JetBrains Exposed, Flyway (Database Migrations)
* **Configuration:** `kotlin-dotenv` (Environment Variables)
* **Logging:** Logback, SLF4J, Logstash Logback Encoder (Structured JSON Logging with Rolling Policy)
* **Testing:** JUnit 5, MockK, Ktor Client Test

---

## ✨ Key Features

* **Clean Architecture Design:** Strict separation of concerns across Domain business logic, Data access layers, and API presentation layers for high maintainability and testability.
* **Centralized Error Handling:** Global error management via Ktor `StatusPages`, mapping custom exceptions (e.g., `DataNotFoundException`) into standardized JSON `ErrorResponse` payloads with appropriate HTTP status codes.
* **Structured JSON Logging:** Logs formatted as structured JSON using Logstash Encoder, supporting dynamic custom fields and environment-driven `LOG_LEVEL` configuration.
* **Log Rotation & Retention:** Size and time-based rolling policy (`SizeAndTimeBasedRollingPolicy`) with automatic compression (.gz) and storage capacity caps.
* **Database Version Control:** Seamless schema migrations and versioning for SQLite managed by Flyway.
* **Comprehensive Test Suites:** Robust unit and integration test coverage across service logic and API endpoints (`TaskApiTest`, `TaskServiceTest`).

---

## 📂 Project Structure

```text
src/
├── main/
│   ├── kotlin/
│   │   ├── database/    # Database initialization & Exposed table definitions
│   │   ├── di/          # Dependency Injection (Koin) modules
│   │   ├── model/       # Data models, DTOs & Domain entities
│   │   ├── plugins/     # Ktor plugins (StatusPages, Serialization, Monitoring, etc.)
│   │   ├── repository/  # Data access layer & Repository implementations
│   │   ├── routes/      # Ktor route definitions & API endpoints
│   │   ├── service/     # Business logic layer
│   │   ├── utils/       # Utility classes & Helper functions
│   │   └── Main.kt      # Application entry point
│   └── resources/       # Application configurations & logback.xml
└── test/                # Unit & Integration test suites
```

[//]: # (### Description of each folder &#40;for reference&#41;)

[//]: # ()
[//]: # (  * **`database/`**: Manages database initialization, connection setup, and Exposed ORM table definitions.)

[//]: # (  * **`di/`**: Contains Dependency Injection &#40;Koin&#41; modules for wiring up services and repositories.)

[//]: # (  * **`model/`**: Stores data classes, DTOs, and domain entities used across the application.)

[//]: # (  * **`plugins/`**: Configures Ktor plugins such as StatusPages &#40;centralized error handling&#41;, serialization, and routing.)

[//]: # (  * **`repository/`**: Implements the data access layer to interact directly with the database.)

[//]: # (  * **`routes/`**: Defines API endpoints and handles incoming HTTP requests.)

[//]: # (  * **`service/`**: Contains the core business logic and application use cases.)

[//]: # (  * **`utils/`**: Houses helper functions, extension functions, and utility classes.)

[//]: # (  * **`Main.kt`: The main entry point of the application where the Ktor Netty server is started.)

[//]: # (  * **`resources/`**: Stores configuration files and resources such as logback.xml.)

---

## ⚙️ Environment Configuration

The application relies on environment variables and supports a `.env` file at the root directory (refer to `.env.example`):

```properties
# Application Configuration
APP_ENV=development
APP_NAME=deesoft-kotlin-api
PORT=8080
HOST=0.0.0.0

# Database Configuration
DATABASE_URL=jdbc:sqlite:data/task.db
DATABASE_DRIVER=org.sqlite.JDBC

# Logging Configuration
LOG_LEVEL=INFO
```

---

## 🚀 Getting Started

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/deesoft/kotlin-api.git
   cd kotlin-api
   ```

2. **Run the Application:**
    * Via Gradle:
      ```bash
      ./gradlew run
      ```
    * Or run directly from `Main.kt` in IntelliJ IDEA.

3. **Run Tests:**
   ```bash
   ./gradlew test
   ```

---

## 📌 API Endpoints

Below is the summary of available RESTful API endpoints for managing tasks:

| HTTP Method | Endpoint | Description | Sample Request Payload | Status Code |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/tasks` | Retrieve all tasks | *None* | `200 OK` |
| **GET** | `/tasks/{id}` | Retrieve a specific task by ID | *None* | `200 OK` / `404 Not Found` |
| **POST** | `/tasks` | Create a new task | See example below | `201 Created` / `400 Bad Request` |
| **PUT** | `/tasks/{id}` | Update an existing task | See example below | `200 OK` / `404 Not Found` |
| **PATCH** | `/tasks/{id}/complete` | Mark a specific task as completed | *None (Path Parameter only)* | `200 OK` / `404 Not Found` |
| **DELETE** | `/tasks/{id}` | Delete a task by ID | *None* | `204 No Content` / `404 Not Found` |


---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.