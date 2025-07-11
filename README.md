SkillForge is a full-stack career roadmap and skill-building platform built with Java (Spring Boot) backend, React frontend, and MySQL database.

It leverages AI to generate personalized learning plans and project recommendations for users, helping them navigate their tech career paths effectively.



Technology Stack
Backend: Java 17, Spring Boot, Spring Security, Hibernate, JWT

Frontend: React

Database: MySQL

AI Integration: OpenAI GPT API for personalized learning plans and project recommendations



backend/
├── src/
│   ├── main/
│   │   ├── java/com/skillforge/backend/
│   │   │   ├── SkillforgeBackendApplication.java
│   │   │   ├── config/             → Spring Security, CORS, JWT configurations
│   │   │   ├── controller/         → REST controllers (e.g. UserController)
│   │   │   ├── dto/                → Data Transfer Objects
│   │   │   ├── exception/          → GlobalExceptionHandler, custom exceptions
│   │   │   ├── model/              → Entities (User, LearningPath, Task, etc.)
│   │   │   ├── repository/         → Spring Data JPA repositories
│   │   │   ├── service/            → Business logic services
│   │   │   └── util/               → JWTUtils, mappers, helper classes
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/ (optional)
│   └── test/java/com/skillforge/backend/  → Unit and integration tests



