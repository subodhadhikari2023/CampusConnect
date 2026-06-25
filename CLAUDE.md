# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CampusConnect is a Spring Boot web application — a collaborative learning platform for academic institutions with role-based access for Admins, HODs (Heads of Department), Teachers, and Students.

## Commands

All commands run from the repository root.

```bash
mvn spring-boot:run        # dev mode with hot reload
mvn package -DskipTests    # build
mvn test                   # run all tests
mvn test -Dtest=ClassName  # run single test class
mvn clean package          # clean build
```

The app runs at `http://localhost:8080`.

## Database Setup

Requires MySQL Server 8.0+. Run the full schema and seed data as root:

```bash
mysql -u root -p < sql-scripts/databaseScript.sql
```

**Dummy credentials** (all use password `password`):
- Admin: `admin1`, `admin2`
- HOD: `hod1`, `hod2`
- Teacher: `teacher1`, `teacher2`
- Student: `student1`, `student2`

## Configuration

`src/main/resources/application.properties`:

- `spring.datasource.url/username/password` — DB connection (defaults to `campusConnect`@`localhost`)
- `file.upload-dir` — absolute path for uploaded files (default `D:/Uploads/` — **change on Linux/Mac**)

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

| Role | URL prefix | Home |
|------|-----------|------|
| ROLE_ADMIN | `/admin/**` | `/admin` |
| ROLE_HOD | `/hod/**` | `/hod` |
| ROLE_TEACHER | `/teacher/**` | `/teacher` |
| ROLE_STUDENT | `/student/**` | `/student` |

Authentication uses `JdbcUserDetailsManager` against the `members` table (credentials) and `roles` table (authorities).

### Database Schema

Key tables:
- `members` — users (PK: `user_id`), with `dept_id` FK to `department`
- `roles` — one role per user (FK to `members`)
- `department` — departments
- `department_details` — maps members to departments with a role label
- `course_details` — courses per department
- `semester` — semesters scoped per course
- `subject_details` — subjects per course+semester
- `file_data` — uploaded file metadata; files on disk at `{upload-dir}/{departmentId}/{fileRole}/{courseId}/{semesterId}/{subjectId}/`
- `announcements` — department-scoped notices posted by HODs
- `teacher_subject` — one-to-one assignment of a teacher to a subject

### Passwords

Stored with Spring Security's `{noop}` prefix (dev/test only). Prepended explicitly in `AdminController.saveUser()`.

## Documentation Rules

All new code **must** include Javadoc before the PR is opened. No exceptions.

### What requires Javadoc

- Every `public` and `protected` class, interface, and enum
- Every `public` and `protected` method
- Every `public` and `protected` field (unless self-evident from the name alone)

### Rules

- First sentence is the summary — one line only
- `@param` / `@return` / `@throws` only when they add information beyond the signature
- Do not restate the method name in prose

### Enforcement

- **Within Claude Code sessions:** a `PostToolUse` hook fires after every `.java` edit and warns about missing Javadoc
- **At commit time:** `.git/hooks/pre-commit` blocks commits if staged Java files have undocumented public/protected members
- Hook script: `.claude/hooks/check-javadoc.py`

## Project Structure

The Maven project (`pom.xml`, `src/`, `Dockerfile`, `sql-scripts/`) lives at the **repository root**.
The `archive/` directory holds historical files no longer in the active source tree.
