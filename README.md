# Job Portal System

A job portal backend built with Spring Boot 4.1, Spring Security, JWT authentication, and Redis caching. It connects job seekers with employers — job seekers can search and apply for roles, employers can post openings and manage applicants, and admins can oversee the whole platform.

## Table of Contents

* [Overview](#overview)
* [Features](#features)
* [Tech Stack](#tech-stack)
* [Architecture](#architecture)
* [Prerequisites](#prerequisites)
* [Installation](#installation)
* [Configuration](#configuration)
* [Running the Application](#running-the-application)
* [API Documentation](#api-documentation)
* [Database Schema](#database-schema)
* [Project Structure](#project-structure)
* [Security](#security)
* [Performance](#performance)
* [Troubleshooting](#troubleshooting)
* [Contributing](#contributing)
* [License](#license)

## Overview

This is a Spring Boot backend for a job portal, built around three user roles:

* **Job seekers** search listings, apply to jobs, and manage their profile and resumes
* **Employers** post jobs, review applications, and manage hiring
* **Admins** manage users, monitor activity, and pull reports

Registration goes through email OTP verification, auth is handled with JWT, and Redis is used to cache frequently-read data so the app doesn't hammer the database on every request.

## Features

**Authentication \& security**

* Email OTP verification on signup
* Passwords hashed with BCrypt
* JWT-based auth, tokens expire after 1 hour
* CSRF protection
* Role-based access control (`ADMIN`, `USER`, `JOB_SEEKER`,`RECRUITER`)

**User management**

* Registration and profile management
* Password reset
* Account status tracking (`ACTIVE`, `INACTIVE`, `SUSPENDED`)

**Jobs**

* Employers post and manage listings
* Search and filtering by category, location, etc.
* Application tracking with status (`PENDING`, `ACCEPTED`, `REJECTED`)
* Job expiration handling

**Resumes**

* Upload/download, multiple resumes per user
* Basic validation on upload

**Reporting**

* Application and job-posting stats
* User activity tracking

**Performance**

* Redis caching on hot paths
* Connection pooling
* Lazy loading + pagination for large result sets

## Tech Stack

|Layer|Choice|
|-|-|
|Framework|Spring Boot 4.1.0|
|Security|Spring Security|
|Data access|Spring Data JPA|
|Database|SQL Server|
|Caching|Redis|
|Auth tokens|JJWT 0.12.3|
|Password hashing|BCrypt|
|Email|Spring Mail (Gmail SMTP)|
|Boilerplate|Lombok 1.18.42|
|Build|Maven|
|Language|Java 25|

Jackson, Jakarta Persistence, and the Servlet API round out the rest of the dependencies.

## Architecture

Fairly standard layered setup — controllers handle HTTP, a JWT filter sits in front of the security layer, services hold the business logic, and repositories talk to the database. Redis and Gmail SMTP sit off to the side as external dependencies the service layer calls into.

```
┌─────────────────────────────────────┐
│        Client Layer (Web/Mobile)     │
└──────────────┬───────────────────────┘
               │ HTTP/REST
┌──────────────▼───────────────────────┐
│        REST Controllers Layer         │
│  UserController · JobController       │
│  ApplicationController · ResumeController │
└──────────────┬───────────────────────┘
               │
┌──────────────▼───────────────────────┐
│     Security \& JWT Filter Layer       │
└──────────────┬───────────────────────┘
               │
┌──────────────▼───────────────────────┐
│      Service Layer (Business logic)   │
│  UserService · JobService · EmailService │
└──────────────┬───────────────────────┘
               │
┌──────────────▼───────────────────────┐
│    Repository Layer (Data access)     │
│  UserRepository · JobRepository       │
└──────────────┬───────────────────────┘
               │
    ┌──────────┼──────────┐
    ▼          ▼          ▼
 SQL Server  Redis    Gmail SMTP
```

## Prerequisites

**Local tools**

* JDK 17+
* Maven 3.8+
* Git

**External services**

* SQL Server 2019+ running on `localhost:1433`
* Redis 6.0+ (local or hosted)
* A Gmail account with an app password, for sending OTP emails

Any Java-friendly IDE works — IntelliJ, VS Code with the Java extensions, or Eclipse.

## Installation

```bash
git clone https://github.com/yourusername/JobPortalSystem.git
cd JobPortalSystem
mvn clean install
```

Create the database:

```sql
CREATE DATABASE JobPortalSystem;
```

Start Redis (same command on Windows/Linux/Mac):

```bash
redis-server
```

For email: turn on 2FA on the Gmail account you're using, then generate an app password at https://myaccount.google.com/apppasswords. You'll need that 16-character password for the config step below.

## Configuration

Copy the example config and fill in your own values:

```bash
cp src/main/resources/application-example.properties src/main/resources/application-dev.properties
```

`src/main/resources/application-dev.properties`:

```properties
# Application
spring.application.name=JobPortalSystem
spring.profiles.active=dev
server.servlet.context-path=/jobportal

# Database
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=JobPortalSystem;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YOUR\_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Redis
spring.data.redis.url=redis://default:PASSWORD@host:14571

# Email (Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=YOUR\_APP\_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# JWT
jwt.secret.key=TaK+HaV^uvCHEFsEVfypW#7g9^k\*Z8$V
```

Don't commit this file with real credentials in it — see [Security](#security) below.

## Running the Application

**Dev mode**

```bash
mvn clean spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

or build and run the jar directly:

```bash
mvn clean package -DskipTests
java -jar target/JobPortalSystem-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

**Production**

```bash
mvn clean package -DskipTests

java -jar target/JobPortalSystem-0.0.1-SNAPSHOT.jar \\
  --spring.profiles.active=prod \\
  --spring.datasource.password=${DB\_PASSWORD} \\
  --jwt.secret.key=${JWT\_SECRET}
```

**Sanity check** — once it's up, this should return a real HTTP status rather than a connection error:

```bash
curl http://localhost:8080/jobportal/users/login
```

## API Documentation

### You can see here All API :
     ``` https://jobportalsystem-production-4cc4.up.railway.app/jobportal/swagger-ui/index.html#/

###In Your Localhost run to :     
### Sign up

```http
POST http://localhost:8080/jobportal/users/signup
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "SecurePassword123"
}
```

`201 Created`:

```json
{
  "message": "OTP sent to your email",
  "email": "john@example.com"
}
```

### Verify OTP

```http
POST http://localhost:8080/jobportal/users/verify-otp
Content-Type: application/json

{
  "email": "john@example.com",
  "otp": "123456"
}
```

`200 OK`:

```json
{
  "userId": 1,
  "email": "john@example.com",
  "message": "User created successfully"
}
```

### Log in

```http
POST http://localhost:8080/jobportal/users/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "SecurePassword123"
}
```

`200 OK`:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "type": "Bearer"
}
```

Then pass the token on subsequent requests:

```http
GET http://localhost:8080/jobportal/jobs
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Database Schema

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created\_at DATETIME DEFAULT GETDATE()
);

CREATE TABLE jobs (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    title VARCHAR(200) NOT NULL,
    description TEXT,
    salary DECIMAL(10, 2),
    location VARCHAR(100),
    category VARCHAR(50),
    posted\_by BIGINT,
    posted\_date DATETIME DEFAULT GETDATE(),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    FOREIGN KEY (posted\_by) REFERENCES users(id)
);

CREATE TABLE applications (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    user\_id BIGINT NOT NULL,
    job\_id BIGINT NOT NULL,
    resume\_id BIGINT,
    status VARCHAR(20) DEFAULT 'PENDING',
    applied\_date DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user\_id) REFERENCES users(id),
    FOREIGN KEY (job\_id) REFERENCES jobs(id)
);
```

## Project Structure

```
JobPortalSystem/
│
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
│
├── src/
│   │
│   ├── main/
│   │   │
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── project/
│   │   │           └── JobPortalSystem/
│   │   │               │
│   │   │               ├── Config/
│   │   │               │   ├── OpenApiConfig.java
│   │   │               │   ├── RedisConfig.java
│   │   │               │   └── SecurityConfig.java
│   │   │               │
│   │   │               ├── Controller/
│   │   │               │   ├── AdminController.java
│   │   │               │   ├── ApplicationController.java
│   │   │               │   ├── JobController.java
│   │   │               │   ├── ReportController.java
│   │   │               │   ├── ResumeController.java
│   │   │               │   └── UserController.java
│   │   │               │
│   │   │               ├── DTO/
│   │   │               │   ├── ApplicationDTO.java
│   │   │               │   ├── ApplicationsResponse.java
│   │   │               │   ├── DashboardDto.java
│   │   │               │   ├── JobDto.java
│   │   │               │   ├── ReportDto.java
│   │   │               │   ├── ReportRequest.java
│   │   │               │   ├── UserDto.java
│   │   │               │   └── VerifyOtpRequestDTO.java
│   │   │               │
│   │   │               ├── Entity/
│   │   │               │   ├── Report.java
│   │   │               │   ├── UserRole.java
│   │   │               │   ├── UserRoleConverter.java
│   │   │               │   ├── UserStatus.java
│   │   │               │   ├── UserStatusConverter.java
│   │   │               │   ├── application.java
│   │   │               │   ├── jobs.java
│   │   │               │   ├── resume.java
│   │   │               │   └── users.java
│   │   │               │
│   │   │               ├── Repository/
│   │   │               │   ├── ApplicationRepository.java
│   │   │               │   ├── JobsRepository.java
│   │   │               │   ├── ReportRepository.java
│   │   │               │   ├── ResumeRepository.java
│   │   │               │   └── UserRepository.java
│   │   │               │
│   │   │               ├── Servies/
│   │   │               │   ├── AdminService.java
│   │   │               │   ├── ApplicationService.java
│   │   │               │   ├── EmailService.java
│   │   │               │   ├── JobsService.java
│   │   │               │   ├── RedisService.java
│   │   │               │   ├── ReportService.java
│   │   │               │   ├── ResumeService.java
│   │   │               │   ├── UserDetailsServiceImpl.java
│   │   │               │   └── UserService.java
│   │   │               │
│   │   │               ├── Utils/
│   │   │               │   └── JwtUtil.java
│   │   │               │
│   │   │               ├── filter/
│   │   │               │   └── Jwtfilter.java
│   │   │               │
│   │   │               └── JobPortalSystemApplication.java
│   │   │
│   │   └── resources/
│   │       ├── application-dev.properties
│   │       ├── application-example.properties
│   │       ├── application-prod.properties
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── project/
│                   └── JobPortalSystem/
│
├── .gitattributes
├── .gitignore
├── HELP.md
├── README.md
├── SETUP.md
├── mvnw
├── mvnw.cmd
└── pom.xml
```

## Security

What's in place:

1. JWT auth with token expiration
2. BCrypt password hashing (salted automatically)
3. CSRF protection via Spring Security
4. Role-based access control (`USER`, `ADMIN`, `RECRUITER`,`JOB\_SEEKER`)
5. Secrets kept out of code, pulled from environment variables in prod
6. Parameterized queries via JPA (no raw SQL string-building)
7. Standard security headers (CSP, X-Frame-Options)

A few things worth actually following:

* Use HTTPS in production, no exceptions
* Validate input server-side, not just in the client
* Keep dependencies patched
* Don't commit real credentials — `application-dev.properties` with actual secrets should never end up in git
* Don't log sensitive data (tokens, passwords, full resumes) in plaintext logs

## Performance

* Redis caches the data that gets read often and changes rarely
* HikariCP handles connection pooling
* Pagination and lazy loading keep large queries from loading everything into memory at once
* Actuator can be turned on for basic health/metrics monitoring:

```properties
management.endpoints.web.exposure.include=health,metrics
```

```bash
curl http://localhost:8080/jobportal/actuator/health
```

## Troubleshooting

**`Connection refused at localhost:1433`**
SQL Server probably isn't running, or the port's wrong. Check with:

```bash
sqlcmd -S localhost -U sa -P your\_password
```

**Redis connection refused**

```bash
redis-server
redis-cli ping   # should return PONG
```

**401 Unauthorized on requests that should work**
Double check the token is actually being sent:

```bash
curl -H "Authorization: Bearer YOUR\_TOKEN" http://localhost:8080/jobportal/users/profile
```

**SMTP authentication failed**
Almost always means the Gmail app password wasn't generated correctly, or 2FA isn't on. Regenerate the app password and re-check `application-dev.properties`.

**`Address already in use: 8080`**
Something else is already on that port. Change it in `application.properties`:

```properties
server.port=9090
```

## Contributing

1. Fork the repo
2. Create a branch: `git checkout -b feature/your-feature`
3. Commit: `git commit -m "feat: add feature"`
4. Push: `git push origin feature/your-feature`
5. Open a PR

Stick to the Google Java Style Guide, use meaningful names, comment the non-obvious parts, and add tests for new features.

## License

MIT — see the LICENSE file for details.

## Support

* Issues: https://github.com/rajsutreja/JobPortalSystem/issues
* Discussions: https://github.com/rajsutreja/JobPortalSystem/discussions
* Email: support@jobportalssystem.com

\---

**Status:** Active development
**Last updated:** August 2026

