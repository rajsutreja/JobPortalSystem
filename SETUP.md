# Job Portal System - Setup Guide

## 📋 Prerequisites
- Java 17+
- Maven 3.8+
- SQL Server
- Redis
- Gmail Account (for OTP emails)

## 🔐 Configuration Setup

### 1. Create Environment Configuration Files

Copy the example properties file:
```bash
cp src/main/resources/application-example.properties src/main/resources/application-dev.properties
cp src/main/resources/application-example.properties src/main/resources/application-prod.properties
```

### 2. Configure application-dev.properties

Edit `src/main/resources/application-dev.properties` and add your credentials:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false


spring.data.redis.url=${REDIS_URL}

spring.mail.host=${MAIL_HOST}
spring.mail.port=${MAIL_PORT}
spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

jwt.secret.key=${JWT_SECRET_KEY}
```

### 3. Configure application-prod.properties

For production, use environment variables or a secure vault instead of hardcoding credentials.

## ⚠️ Important Security Notes

- **NEVER commit `application-dev.properties` or `application-prod.properties`** to git
- **NEVER commit files with database credentials, JWT secrets, or API keys**
- Use `.gitignore` to exclude sensitive files
- For production, use environment variables:
  ```bash
  java -jar app.jar \
    --spring.datasource.password=${DB_PASSWORD} \
    --jwt.secret.key=${JWT_SECRET}
  ```

## 🚀 Running the Application

### Development
```bash
mvn clean install
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Production
```bash
mvn clean package
java -jar target/JobPortalSystem-1.0.jar --spring.profiles.active=prod
```

## 📡 API Endpoints

### User Management
- **Signup:** `POST /jobportal/users/signup`
- **Verify OTP:** `POST /jobportal/users/verify-otp`
- **Login:** `POST /jobportal/users/login`

## 🔑 Environment Variables (Recommended for Production)

```bash
# Database
DB_URL=jdbc:sqlserver://host:1433;...
DB_USERNAME=sa
DB_PASSWORD=your_password

# Redis
REDIS_URL=redis://host:14571

# Email
MAIL_USERNAME=email@gmail.com
MAIL_PASSWORD=app_password

# JWT
JWT_SECRET=your_secret_key_here
```

## 📖 Additional Documentation

- Spring Boot: https://spring.io/projects/spring-boot
- JWT: https://jwt.io/
- Redis: https://redis.io/
