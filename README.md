# skeleton-backend

This is a skeleton backend project that can be used as a starting point for your own projects.
It includes a basic structure and some common features that are often needed in backend applications.
It basically provides the system for managing gym(company) information, including gym membership and members.

## Features

- User authentication and authorization with JWT
- Gym (Company) management system
- Advanced error handling and validation
- Comprehensive logging setup
- Database migration with Liquibase
- Security implementation with Spring Security
- Complete testing framework

## Tech Stack

- **Languages**: Java 17, Kotlin
- **Framework**: Spring Boot 3.4.0
- **Build Tool**: Gradle
- **Database**: MySQL with JPA
- **Query Builder**: QueryDSL 5.0.0
- **Security**: Spring Security + JWT (jjwt 0.11.5)
- **Migration**: Liquibase 4.30.0
- **Testing**: JUnit 5, Spring Security Test
- **Development**: Lombok, Spring Boot DevTools

## Key Dependencies

- Spring Boot Starter Web, Data JPA, Security, Validation
- QueryDSL for type-safe queries
- JWT for token-based authentication
- Liquibase for database version control
- MySQL connector
- Comprehensive testing utilities

## Getting Started

### Prerequisites

- JDK 17 or higher
- Docker
- Git

### Installation & Setup

1. **Clone the repository**
```bash
git clone https://github.com/jinaparkdev/skeleton-backend.git
cd skeleton-backend
```

2. **Configure the database**
```
# Start MySQL database using Docker Compose
cd docker
docker-compose -f docker.yml up -d

# Verify MySQL is running
docker ps
```

3. **Build and run the application**
```bash
./gradlew build
./gradlew bootRun
```
