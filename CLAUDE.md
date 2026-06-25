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

See `ARCHITECTURE.md` for layer diagram, ER diagram, class diagram, and full route map.

Authentication uses `JdbcUserDetailsManager` against `members` (credentials) and `roles` (authorities). Passwords stored with `{noop}` prefix (dev only); prepended in `AdminController.saveUser()`.

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
