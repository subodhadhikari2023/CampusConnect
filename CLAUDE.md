# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CampusConnect is a Spring Boot web application — a collaborative learning platform for academic institutions with role-based access for Admins, HODs (Heads of Department), Teachers, and Students.

## Commands

All commands run from the repository root.

```bash
# Run the application (dev mode with hot reload via spring-boot-devtools)
mvn spring-boot:run

# Build (skip tests)
mvn package -DskipTests

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=ClassName

# Clean build
mvn clean package
```

The app runs at `http://localhost:8080`.

## Database Setup

Requires MySQL Server 8.0+. Run the full schema and seed data as root:

```bash
mysql -u root -p < sql-scripts/databaseScript.sql
```

This creates the `campusConnect` database, the `campusConnect` MySQL user (password from `DB_PASSWORD` in your `.env`), all tables, and dummy users. The `departments.sql` script can be run separately for additional department seed data.

**Dummy credentials** (all use password `password`):
- Admin: `admin1`, `admin2`
- HOD: `hod1`, `hod2`
- Teacher: `teacher1`, `teacher2`
- Student: `student1`, `student2`

## Configuration

`src/main/resources/application.properties` — two values typically need updating per environment:

- `spring.datasource.url/username/password` — DB connection (defaults to `campusConnect`@`localhost`)
- `file.upload-dir` — absolute path where uploaded files are stored (default is `D:/Uploads/` — **must be changed on Linux/Mac**)

## Architecture

### Layer Structure

```
controller/  → Spring MVC @Controller classes (one per role)
service/     → Business logic interfaces + implementations
repository/  → Spring Data JPA repositories (extend JpaRepository)
entities/    → JPA @Entity classes mapping to DB tables
dto/         → DTOs (currently FileUploadDTO for file uploads)
config/      → Spring Security configuration
exceptions/  → Custom exceptions and global exception handler
templates/   → Thymeleaf HTML templates, organized by role subdirectory
static/      → CSS, JS, images (under loginResources/ for unauthenticated access)
```

### Role-Based Routing

Spring Security enforces URL-prefix authorization. After login, `CustomAuthenticationSuccessHandler` redirects to the role's home:

| Role | URL prefix | Home |
|------|-----------|------|
| ROLE_ADMIN | `/admin/**` | `/admin` |
| ROLE_HOD | `/hod/**` | `/hod` |
| ROLE_TEACHER | `/teacher/**` | `/teacher` |
| ROLE_STUDENT | `/student/**` | `/student` |

Authentication uses `JdbcUserDetailsManager` with custom queries against the `members` table (credentials) and `roles` table (authorities).

### Database Schema

Key tables and their relationships:
- `members` — users (PK: `user_id`), with `dept_id` FK to `department`
- `roles` — one role per user (FK to `members`)
- `department` — departments
- `department_details` — maps members to departments with a role label
- `course_details` — courses per department
- `semester` — semesters (global, not per department)
- `subject_details` — subjects per course+semester
- `file_data` — uploaded file metadata; actual files live on disk at `{upload-dir}/{departmentId}/{fileRole}/{courseId}/{semesterId}/{subjectId}/`

### File Storage

`StorageServiceImplementation` handles file upload/download. Files are stored on the local filesystem (not in the database). The `file_data` table stores metadata including the path. Server-level HTTP compression is enabled in `application.properties` for common MIME types.

### Passwords

Passwords are stored with Spring Security's `{noop}` prefix for plain-text (dev/test only). The `{noop}` prefix is prepended explicitly in `AdminContoller.saveUser()` when creating new users.

## Documentation Rules

All new code **must** include Javadoc before the PR is opened. No exceptions.

### What requires Javadoc

- Every `public` and `protected` class, interface, and enum
- Every `public` and `protected` method
- Every `public` and `protected` field (unless it is self-evident from the name alone)

### Format

```java
/**
 * One-sentence summary of what this does.
 *
 * @param paramName what this parameter represents
 * @return what is returned, and when it may be null
 * @throws SomeException when and why this is thrown
 */
```

- First sentence is the summary — keep it to one line
- `@param` / `@return` / `@throws` only when they add information beyond the method signature
- Do not restate the method name in prose ("This method saves a user" → bad)

### Enforcement

- **Within Claude Code sessions:** a `PostToolUse` hook fires after every `.java` edit and warns about missing Javadoc
- **At commit time:** `.git/hooks/pre-commit` blocks the commit if staged Java files have undocumented public/protected members
- Hook script: `.claude/hooks/check-javadoc.py`

## Project Structure

The Maven project (`pom.xml`, `src/`, `Dockerfile`, `sql-scripts/`) lives at the **repository root**.
The `archive/` directory holds historical files that are no longer part of the active source tree.
