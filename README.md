Here's a `README.md` file for your updated setup, describing the steps to build and run the Dockerized application.

---

# Flight Services Application

This project is a Spring Boot-based Flight Services application that connects to a MySQL database. The setup is containerized using Docker and orchestrated via Docker Compose. The Docker setup includes a multi-stage build process to ensure an efficient and clean deployment.

## Prerequisites

- **Docker** and **Docker Compose** must be installed on your system.
- Ensure that the `my_service_network` Docker network is created prior to running Docker Compose. You can create this network using:

  ```bash
  docker network create my_service_network
  ```

## Project Structure

- **Dockerfile**: Multi-stage Dockerfile that builds the application using Maven and then packages it in a minimal OpenJDK image.
- **docker-compose.yml**: Defines two services, MySQL and the Spring Boot application, and orchestrates their deployment.

## Configuration Files

### Dockerfile

This is a multi-stage Dockerfile:

1. **Stage 1**: Uses a Maven image to build the Spring Boot application, resulting in a JAR file.
2. **Stage 2**: Uses an OpenJDK 21 slim image to run the JAR file, creating a lightweight and efficient final image.

```Dockerfile
# Use an official Maven image with OpenJDK 21 for building
FROM maven AS build

# Set the working directory
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create the final image with OpenJDK 21
FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app
# Copy the jar file from the build stage

COPY --from=build /app/target/flightservices-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application runs on
EXPOSE 8080
	
# Run the application
ENTRYPOINT ["java","-jar","app.jar"]
```

### docker-compose.yml

Defines the services for the application:

- **mysql**: Configures MySQL with environment variables for database credentials, and sets up a volume to store data persistently.
- **springboot-app**: Deploys the Spring Boot application, connects to MySQL, and maps the application port.

```yaml
version: '3.8'

services:
  mysql:
    image: mysql
    container_name: mysql-docker
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: servicedb
      MYSQL_USER: bindisha
      MYSQL_PASSWORD: password
    ports:
      - "3306:3306"
    networks:
      - my_service_network
    volumes:
      - mysql_data:/var/lib/mysql  

  springboot-app:
    image: bindisha/flightservicerepo:v1
    container_name: flightservice-container
    ports:
      - "9091:9091"
    environment:
      DB_HOST: mysql-docker
      DB_PORT: 3306
      DB_NAME: reservation
      DB_USER: bindisha
      DB_PASSWORD: password
    depends_on:
      - mysql
    networks:
      - my_service_network

networks:
  my_service_network:
     external: true

volumes:
  mysql_data:
```

## Instructions

### 1. Build the Spring Boot Application Docker Image

To build the Docker image for the application, you need to run:

```bash
docker build -t bindisha/flightservicerepo:v1 .
```

This command builds the application using the multi-stage Dockerfile.

### 2. Start the Services Using Docker Compose

Once the image is built, use Docker Compose to start both the MySQL and Spring Boot application containers:

```bash
docker-compose up -d
```

The `-d` flag runs the containers in detached mode.

### 3. Accessing the Application

- **Spring Boot Application**: Access it at `http://localhost:9091`.
- **MySQL Database**: Connect using `localhost:3306`.

### Environment Variables

In the `docker-compose.yml` file, the following environment variables configure the database connection for the Spring Boot application:

| Variable    | Description                 |
|-------------|-----------------------------|
| DB_HOST     | Hostname of the MySQL server|
| DB_PORT     | Port for MySQL              |
| DB_NAME     | Database name               |
| DB_USER     | MySQL user                  |
| DB_PASSWORD | MySQL user password         |

### Stopping the Services

To stop and remove the containers, use:

```bash
docker-compose down
```

### Data Persistence

The MySQL data is stored in a Docker volume named `mysql_data`, ensuring that data persists across container restarts or deletions.

### Troubleshooting

- **Network Issues**: Ensure the `my_service_network` Docker network is created before running the setup.
- **Database Connection**: If the Spring Boot application cannot connect to the MySQL database, ensure that the MySQL container is running and ready before starting the Spring Boot container. The `depends_on` option in Docker Compose helps manage this, but there might still be a delay.

--- 

This `README.md` provides a comprehensive guide for setting up, running, and troubleshooting the Dockerized application.
