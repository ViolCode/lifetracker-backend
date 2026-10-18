LifeTracker — Backend

LifeTracker is intended to support event planning and expense tracking through a separate backend service. This repository contains a Java application built with Spring Boot and Gradle.
The frontend is maintained separately in the LifeTracker frontend repository.

Technology stack:
- Java (the Gradle build currently configures Java 25)
- Spring Boot
- Spring Web MVC
- Gradle
- JUnit Platform test configuration
- GitLab CI configuration

Getting started:
Clone the repository and enter its root directory.

On macOS or Linux:
./gradlew bootRun

On Windows:
.\gradlew.bat bootRun

Build and test

Build the project:
./gradlew build

Run tests:
./gradlew test

Related repository:
- LifeTracker frontend
