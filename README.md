# TuneInsights — Backend

TuneInsights is a full-stack music catalog and personal music insights application. This repository contains the Spring Boot backend responsible for authentication, music search, library management, analytics, password reset functionality, and AI-powered music recommendations.

## Live API

**Backend:** https://music-catalog-insights-backend-3uly.onrender.com

**Frontend:** https://music-catalog-insights-frontend.vercel.app/

---

## Features

* User registration
* User login
* JWT-based authentication
* Password encryption using BCrypt
* Password reset functionality
* Password reset token management
* Music search
* Personal music library management
* Song and library relationships
* Music analytics data
* AI-powered personalized recommendations
* PostgreSQL database integration
* CORS configuration for frontend communication
* REST API architecture

---

## Tech Stack

* Java
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* PostgreSQL
* Neon PostgreSQL
* JWT
* BCrypt
* Groq API
* Maven
* Render

---

## Architecture

The backend follows a layered architecture.

```text
Client / Frontend
       │
       ▼
REST Controllers
       │
       ▼
Services
       │
       ▼
Repositories
       │
       ▼
JPA Entities
       │
       ▼
PostgreSQL Database
```

Security is handled through Spring Security and JWT authentication.

The AI recommendation functionality communicates with the Groq API through the backend.

---

## Project Structure

```text
backend/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/example/musiccataloginsights/
│   │   │
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── entity/
│   │   │       ├── repository/
│   │   │       ├── security/
│   │   │       └── service/
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── .mvn/
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

---

# Entity Design

The application uses the following main entities:

* `User`
* `Song`
* `Library`
* `LibrarySong`
* `PasswordResetToken`

---

## User

The `User` entity represents a registered application user.

The user is responsible for:

* Authentication
* Personal library ownership
* AI recommendation generation
* Profile information

Typical user information includes:

```text
User
├── id
├── username
├── email
└── password
```

Passwords are not stored as plain text. They are encrypted using BCrypt before being stored in the database.

---

## Song

The `Song` entity represents a music item available in the application's catalog.

Songs contain the information required to:

* Display search results
* Identify a music track
* Store songs in user libraries
* Provide data for analytics
* Support AI recommendations

---

## Library

The `Library` entity represents a user's personal music library.

A user can have a personal library containing songs that they have saved.

Conceptually:

```text
User
 │
 │ owns
 ▼
Library
```

---

## LibrarySong

The `LibrarySong` entity represents the relationship between a library and a song.

Instead of directly storing a many-to-many relationship between `Library` and `Song`, a separate entity is used.

```text
Library
   │
   │
   ▼
LibrarySong
   │
   │
   ▼
Song
```

This design provides more flexibility because the relationship can later contain additional information such as:

* Date added
* Listening count
* Favorite status
* User-specific metadata

The `LibrarySong` entity therefore acts as the association between a user's library and individual songs.

---

## PasswordResetToken

The `PasswordResetToken` entity is used to support password recovery.

The general flow is:

```text
User
 │
 │ Forgot Password
 ▼
Backend
 │
 │ Generate Reset Token
 ▼
PasswordResetToken
 │
 │ Validate Token
 ▼
Reset Password
```

The reset token allows the application to verify that a password reset request is valid before allowing a user to change their password.

---

# Database Schema

The main database structure can be represented conceptually as:

```text
┌──────────────┐
│    USERS     │
├──────────────┤
│ id           │
│ username     │
│ email        │
│ password     │
└──────┬───────┘
       │
       │
       ▼
┌──────────────┐
│   LIBRARY    │
├──────────────┤
│ id           │
│ user_id      │
└──────┬───────┘
       │
       │
       ▼
┌──────────────┐
│ LIBRARY_SONG │
├──────────────┤
│ id           │
│ library_id   │
│ song_id      │
└──────┬───────┘
       │
       │
       ▼
┌──────────────┐
│     SONG     │
├──────────────┤
│ id           │
│ song data    │
└──────────────┘


┌──────────────────────┐
│ PASSWORD_RESET_TOKEN │
├──────────────────────┤
│ id                   │
│ token                │
│ expiration            │
│ user relationship     │
└──────────────────────┘
```

The exact database columns are managed through the JPA entity mappings.

---

# Authentication and Security

The backend uses Spring Security with JWT authentication.

## Login Flow

```text
Frontend
   │
   │ POST /users/login
   ▼
UserController
   │
   ▼
Authentication Service
   │
   ├── Find User
   ├── Verify Password
   └── Generate JWT
          │
          ▼
       Frontend
```

For protected endpoints, the frontend sends:

```text
Authorization: Bearer <JWT_TOKEN>
```

The `JwtAuthenticationFilter` intercepts requests and:

1. Reads the `Authorization` header.
2. Checks for a Bearer token.
3. Extracts the username/email from the JWT.
4. Loads the corresponding user.
5. Validates the JWT.
6. Creates an authenticated Spring Security context.
7. Allows the request to continue.

The application uses stateless session management.

---

# Password Security

User passwords are encrypted using BCrypt.

The backend does not store plain-text passwords.

Conceptually:

```text
User Password
      │
      ▼
BCryptPasswordEncoder
      │
      ▼
Encrypted Password
      │
      ▼
PostgreSQL
```

---

# Password Reset

The application provides password recovery functionality.

The general flow is:

```text
User
 │
 │ Forgot Password
 ▼
Backend
 │
 │ Generate secure token
 ▼
PasswordResetToken
 │
 │ Token validation
 ▼
Reset Password
 │
 ▼
BCrypt Hash
 │
 ▼
Updated User Password
```

The reset token is used to validate the password reset process before changing the user's password.

---

# AI-Powered Music Recommendations

One of the main features of TuneInsights is personalized AI-powered music recommendations.

The backend analyzes the songs saved in the user's library and sends relevant information to the Groq API.

The general process is:

```text
User
 │
 │ Requests AI Recommendations
 ▼
AiService
 │
 │ Retrieve User
 ▼
LibrarySongService
 │
 │ Get User's Library Songs
 ▼
Build Recommendation Context
 │
 ▼
Groq API
 │
 │ Generate Recommendations
 ▼
AI Response
 │
 ▼
Backend Response
 │
 ▼
Frontend
```

The AI feature can provide recommendations based on the user's existing music collection.

If the user's library is empty, the backend returns an appropriate response informing the user that songs need to be added before personalized recommendations can be generated.

If the library does not contain enough usable music information, the backend returns a fallback response instead of generating unreliable recommendations.

---

# API Endpoints

The main API endpoints include functionality for:

## User Management

```text
POST /users
```

Creates a new user.

```text
POST /users/login
```

Authenticates a user and returns a JWT.

```text
POST /users/forgot-password
```

Starts the password reset process.

```text
POST /users/reset-password
```

Resets the user's password using a valid reset token.

---

## Song Search

```text
GET /songs/search
```

Searches the music catalog and returns matching songs.

---

## Library

The library API provides functionality to:

* Create or access user libraries
* Add songs
* Remove songs
* Retrieve songs belonging to a user
* Manage user-specific music collections

---

## Analytics

The backend provides data required by the frontend to display music analytics and insights.

The analytics functionality can be used to understand patterns within a user's saved music collection.

---

## AI

The AI functionality provides personalized recommendations based on the user's library.

The backend communicates with Groq rather than exposing the Groq API key directly to the frontend.

---

# Environment Variables

The application uses environment variables for sensitive configuration.

```env
DB_URL=your_database_url
DB_USERNAME=your_database_username
DB_PASSWORD=your_database_password

GROQ_API_KEY=your_groq_api_key

JWT_SECRET=your_jwt_secret
```

For local development, these variables can be configured through the application's environment.

For production, the values are configured through the deployment platform's environment variable settings.

### Security

Sensitive values such as:

* Database passwords
* JWT secrets
* Groq API keys

must never be committed to GitHub.

---

# Database

The application uses PostgreSQL as its relational database.

The production database is hosted using Neon PostgreSQL.

The Spring Boot application connects to the database through environment variables.

The database schema is managed using JPA and Hibernate.

The application uses:

```properties
spring.jpa.hibernate.ddl-auto=update
```

This allows Hibernate to update the database schema based on entity changes during development and deployment.

For production systems with strict database migration requirements, a migration tool such as Flyway or Liquibase could be introduced.

---

# Running the Backend Locally

## 1. Clone the repository

```bash
git clone https://github.com/blazing-cell/music-catalog-insights-backend
```

## 2. Navigate to the backend

```bash
cd backend
```

## 3. Configure environment variables

Configure:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
GROQ_API_KEY
JWT_SECRET
```

## 4. Run the application

Using Maven:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The backend will run on:

```text
http://localhost:8080
```

---

# Running Tests

The backend includes automated tests for application functionality and services.

Run tests using:

```bash
./mvnw test
```

On Windows:

```bash
mvnw.cmd test
```

The project uses JUnit and Mockito for testing.

The tests cover service-level functionality including:

* User-related operations
* Library functionality
* Song functionality
* AI recommendation behavior
* Empty library handling
* Missing user handling
* Invalid or unusable library data

---

# Deployment

The backend is deployed on Render.

Production API:

https://music-catalog-insights-backend-3uly.onrender.com

The backend connects to the production Neon PostgreSQL database using environment variables configured in Render.

The production frontend communicates with this backend using:

```text
https://music-catalog-insights-backend-3uly.onrender.com
```

---

# CORS

The backend is configured to allow requests from the production frontend:

```text
https://music-catalog-insights-frontend.vercel.app
```

The backend also supports the local frontend during development:

```text
http://localhost:3000
```

CORS is required because the frontend and backend are deployed separately.

---

# Design Decisions and Trade-offs

## Spring Boot

Spring Boot was selected because it provides a strong ecosystem for building REST APIs, security, database access, and enterprise-style Java applications.

## PostgreSQL

PostgreSQL was selected because the application has relational data involving users, libraries, songs, and library-song relationships.

## Separate LibrarySong Entity

A dedicated `LibrarySong` entity was used instead of a simple many-to-many relationship.

This provides greater flexibility for adding relationship-specific fields in the future.

## JWT Authentication

JWT provides stateless authentication suitable for a separately deployed frontend and backend.

**Advantages:**

* Stateless authentication
* Easy REST API integration
* Scalable authentication model
* No server-side session storage

**Trade-off:**

Token management on the client requires careful security considerations.

## Groq API

The Groq API was selected to provide AI-powered music recommendations without requiring the application to host its own large language model.

**Advantages:**

* Fast AI inference
* Simple API integration
* No model hosting infrastructure required

**Trade-off:**

The AI feature depends on an external API and its availability, pricing, and usage limits.

## Neon PostgreSQL

Neon was used as the production PostgreSQL provider.

**Advantages:**

* Managed PostgreSQL
* Easy cloud deployment
* Suitable for small applications and projects
* Easy integration with Render

**Trade-off:**

The application depends on an external cloud database provider.

## Render

Render was selected to deploy the Spring Boot backend.

**Advantages:**

* Simple deployment from GitHub
* Environment variable management
* Easy integration with Git-based workflows

**Trade-off:**

Free or lower-tier hosting may have cold starts, which can cause the backend to take some time to respond after a period of inactivity.

---

# Future Improvements

Potential future improvements include:

* Use Flyway or Liquibase for database migrations
* Improve AI recommendation quality
* Add recommendation history
* Add music listening history
* Add playlist support
* Add advanced analytics
* Improve password reset email delivery
* Add refresh tokens
* Move JWT handling to secure HttpOnly cookies
* Add rate limiting
* Add API documentation using OpenAPI/Swagger
* Add CI/CD pipelines
* Add more comprehensive integration tests
* Add production monitoring and logging

---

# License

This project was developed as a personal full-stack application project for learning and demonstration purposes.
