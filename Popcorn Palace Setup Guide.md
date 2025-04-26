# Popcorn Palace - Movie Ticket Booking API

## 1. Project Overview

Popcorn Palace is a RESTful API backend for a movie ticket booking system, built using Java and Spring Boot. It allows managing movies and their showtimes, and booking tickets for those showtimes.

This project utilizes:
* Java 21
* Spring Boot 3.4.2
* Maven (for building and dependency management)
* PostgreSQL (as the database, managed via Docker Compose)
* JPA/Hibernate (for database interaction)
* Lombok (to reduce boilerplate code)
* MapStruct (for DTO-Entity mapping - implied)
* JUnit 5 (for testing)

## 2. Prerequisites

Before you begin, ensure you have the following installed on your system:
* **Java Development Kit (JDK):** Version 21 or later.
* **Maven:** Version 3.6 or later.
* **Docker:** Latest version.
* **Docker Compose:** Usually included with Docker Desktop installations.

## 3. Setup

1.  **Clone the Repository:**
    ```bash
    git clone <your-repository-url>
    cd popcorn-palace
    ```
    (Replace `<your-repository-url>` with the actual URL of your Git repository)

## 4. Database Setup (PostgreSQL via Docker)

This project uses Docker Compose to manage the PostgreSQL database container.

1.  **Navigate to the Project Root:** Make sure your terminal is in the `popcorn-palace` directory (where `docker-compose.yml` is located).
2.  **Start the Database Container:**
    ```bash
    docker-compose up -d db
    ```
    * This command specifically starts *only* the database service (`db`) defined in `docker-compose.yml` and runs it in the background (`-d`).
    * The database will be accessible from your host machine on port **5433** (as configured in `docker-compose.yml`) and internally within the Docker network on port `5432`.

3.  **Stopping the Database:**
    ```bash
    docker-compose down -v
    ```
    *(Use `-v` to also remove the anonymous volume containing database data if you want a completely fresh start next time).*

## 5. Configuration

Key configuration settings are located in `src/main/resources/application.yaml`:

* **Database Connection:** Configured to connect to the `db` service within the Docker network (`jdbc:postgresql://db:5432/popcorn-palace`).
* **JPA/Hibernate:**
    * `ddl-auto: none`: Hibernate will not automatically create or update the database schema. Schema management is handled by `schema.sql`.
* **SQL Initialization:**
    * `init.mode: always`: Spring Boot will always attempt to run `schema.sql` (for table creation) and `data.sql` (for initial data seeding, if present) on startup. The `schema.sql` file should be located in `src/main/resources/`.

## 6. Building the Project

You can build the project using Maven. This compiles the code, runs tests, and packages the application into an executable JAR file in the `target/` directory.

1.  **Navigate to the Project Root:**
    ```bash
    cd popcorn-palace
    ```
2.  **Run Maven Build:**
    ```bash
    mvn clean package
    ```
    * This command cleans previous builds and creates `popcorn-palace-0.0.1-SNAPSHOT.jar` (or similar) in the `target` directory.

## 7. Running the Application

There are two main ways to run the application:

**Option 1: Using Docker Compose (Recommended)**

This method builds the application image and runs both the application and database containers together.

1.  **Ensure Database is Stopped (if running separately):**
    ```bash
    docker-compose down -v
    ```
2.  **Build and Start:**
    ```bash
    docker-compose up --build -d
    ```
    * `--build`: Forces Docker Compose to rebuild the application image using the `Dockerfile`.
    * `-d`: Runs the containers in detached mode (in the background).
3.  **View Logs:**
    ```bash
    docker-compose logs -f popcorn-palace
    ```
4.  **Stop:**
    ```bash
    docker-compose down -v
    ```

**Option 2: Running the JAR Directly**

You can run the JAR file built by Maven directly. **Note:** The PostgreSQL database container must be running separately (using `docker-compose up -d db`) for the application to connect successfully.

1.  **Ensure Database is Running:**
    ```bash
    docker-compose up -d db
    ```
2.  **Build the JAR (if not already done):**
    ```bash
    mvn clean package
    ```
3.  **Run the JAR:**
    ```bash
    java -jar target/popcorn-palace-0.0.1-SNAPSHOT.jar
    ```
    (Adjust the JAR filename if necessary).

The application will be accessible at `http://localhost:8080`.

## 8. Testing

Run the unit and integration tests using Maven:

```bash
mvn test

Test reports can be found in the target/surefire-reports directory.

9. API Functionality Overview
The API provides endpoints for managing:

Movies:

Add, update, delete movies.

Fetch all movies.

Showtimes:

Add, update, delete showtimes for specific movies.

Fetch showtime details.

Bookings (Tickets):

Book tickets for a specific showtime and seat.

(Potentially: Fetch booking details - check controller implementations).

Refer to the controller classes (MovieController, ShowtimeController, TicketController) for specific endpoint paths and request/response formats.

10. Project Structure
src/main/java: Contains the main Java source code (controllers, services, models, repositories, mappers, DTOs).

src/main/resources: Contains configuration files (application.yaml), database scripts (schema.sql).