<a name="readme-top"></a>

[![Contributors][contributors-shield]][contributors-url]
[![Issues][issues-shield]][issues-url]
[![GNU License][license-shield]][license-url]
[![CI][ci-shield]][ci-url]
[![Docker][docker-shield]][docker-url]

<h1 align="center">CampusConnect</h1>
<p align="center">A role-based academic management and collaborative learning platform for educational institutions.</p>

<h2>Project Link</h2>

<a href="https://github.com/subodhadhikari2023/CampusConnect">
  <img src="https://img.shields.io/badge/GitHub-black?style=flat-square&logo=github" alt="GitHub Project Link" />
</a>

---

<h2 align="center">Contributors</h2>

<div style="display: flex; justify-content: space-between; flex-wrap: wrap;">

  <div style="text-align: center; flex: 1; min-width: 150px; max-width: 45%;">
    <h2>Subodh Adhikari</h2>
    <img src="./readmeImages/Subodh.jpeg" alt="Subodh" style="width: 150px; height: 150px;">
    <br>
    <a href="https://www.linkedin.com/in/subodh-adhikari-4b811a296/">
      <img src="https://img.shields.io/badge/LinkedIn-blue?style=flat-square&logo=linkedin" alt="LinkedIn Subodh" />
    </a>
    <a href="mailto:subodhadhikari2023@outlook.com">
      <img src="https://img.shields.io/badge/Outlook-blue?style=flat-square&logo=microsoft-outlook" alt="Outlook Subodh" />
    </a>
    <a href="https://github.com/subodhadhikari2023/">
      <img src="https://img.shields.io/badge/GitHub-black?style=flat-square&logo=github" alt="GitHub Link" />
    </a>
  </div>

  <div style="text-align: center; flex: 1; min-width: 150px; max-width: 45%;">
    <h2>Manav Agarwal</h2>
    <img src="./readmeImages/Manav.jpeg" alt="Manav" style="width: 150px; height: 150px;">
    <br>
    <a href="https://www.linkedin.com/in/manav-agarwal-8139b92b8/">
      <img src="https://img.shields.io/badge/LinkedIn-blue?style=flat-square&logo=linkedin" alt="LinkedIn Manav" />
    </a>
    <a href="mailto:manav9981@outlook.com">
      <img src="https://img.shields.io/badge/Outlook-blue?style=flat-square&logo=microsoft-outlook" alt="Outlook Manav" />
    </a>
    <a href="https://github.com/Manav355">
      <img src="https://img.shields.io/badge/GitHub-black?style=flat-square&logo=github" alt="GitHub Link" />
    </a>
  </div>

</div>

---

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about-the-project">About The Project</a></li>
    <li><a href="#screenshots">Screenshots</a></li>
    <li><a href="#features">Features</a></li>
    <li><a href="#built-with">Built With</a></li>
    <li><a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#option-1-docker-recommended">Option 1: Docker (Recommended)</a></li>
        <li><a href="#option-2-manual-setup">Option 2: Manual Setup</a></li>
        <li><a href="#option-3-railway-cloud">Option 3: Railway (Cloud)</a></li>
      </ul>
    </li>
    <li><a href="#environment-variables">Environment Variables</a></li>
    <li><a href="#development-workflow">Development Workflow</a></li>
    <li><a href="#testing">Testing</a></li>
    <li><a href="#cicd-pipeline">CI/CD Pipeline</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

---

## About The Project

**CampusConnect** is a role-based academic management and collaborative learning platform designed for educational institutions. It provides a centralized hub where administrators, department heads, faculty, and students can manage academic workflows, share resources, and collaborate — all within a structured, permission-driven environment.

The platform is built with Spring Boot and Thymeleaf, backed by MySQL, and ships as a multi-stage Docker image deployed to Railway via GitHub Actions.

---

## Screenshots

<div align="center">
<table>
  <tr>
    <td align="center" width="33%">
      <img src="docs/screenshots/landing-page.png" alt="Landing Page" width="260"/>
      <br/><sub><b>Landing Page</b><br/>Hero, feature cards, How It Works</sub>
    </td>
    <td align="center" width="33%">
      <img src="docs/screenshots/login-page.png" alt="Login Page" width="260"/>
      <br/><sub><b>Login Page</b><br/>Clean sign-in card with password toggle</sub>
    </td>
    <td align="center" width="33%">
      <img src="docs/screenshots/admin-dashboard.png" alt="Admin Dashboard" width="260"/>
      <br/><sub><b>Admin Dashboard</b><br/>Live stats, quick actions, recent uploads</sub>
    </td>
  </tr>
</table>
</div>

---

## Features

- **Role-Based Access Control** — Distinct roles (Admin, HOD, Teacher, Student) with scoped permissions across all platform actions
- **Admin Dashboard** — System-wide stats with full CRUD for users and departments
- **HOD Module** — Full course / semester / subject / curriculum management; teacher-subject assignment; department announcements; workload view
- **Resource Library** — Teachers upload lecture slides, videos, notes, programs, audio books, and reference material scoped to their assigned subjects
- **Dept-Scoped Browse** — Teachers and students browse and download resources filtered to their own department
- **File Compression Downloads** — Download resources in original, GZIP, or ZIP format
- **Announcements** — HODs post department notices; visible to all teachers and students in the department
- **Flyway Migrations** — Versioned schema management; schema and seed data applied automatically on first boot
- **Health Endpoint** — `/actuator/health` for uptime monitoring and Docker health checks

---

## Built With

**Application**

[![Spring Boot][SpringBoot6.js]][SpringBoot6-url]
[![Spring Security][SpringSecurity.js]][SpringSecurity-url]
[![JPA Hibernate][JPA.com]][JPA-url]
[![Thymeleaf][Thymeleaf.js]][Thymeleaf-url]
[![MySQL][MySQL.js]][MySQL-url]
[![HTML][HTML.js]][HTML-url]
[![CSS][CSS.js]][CSS-url]
[![JavaScript][Javascript.js]][Javascript-url]

**DevOps & CI/CD**

[![Docker][DockerBadge.js]][Docker-url]
[![GitHub Actions][GHA.js]][GHA-url]
[![GHCR][GHCR.js]][GHCR-url]
[![Flyway][Flyway.js]][Flyway-url]
[![Actuator][Actuator.js]][Actuator-url]

**Testing**

[![JUnit 5][JUnit.js]][JUnit-url]
[![Mockito][Mockito.js]][Mockito-url]
[![H2][H2.js]][H2-url]
[![Spring Security Test][SST.js]][SST-url]

---

## Getting Started

Choose the setup method that suits your environment.

---

### Option 1: Docker (Recommended)

No manual database setup required. Docker Compose starts MySQL 8.0 and the Spring Boot app; Flyway creates all tables and seeds data automatically on first boot.

**Prerequisites:** Docker and Docker Compose installed.

```bash
# 1. Clone the repository
git clone https://github.com/subodhadhikari2023/CampusConnect.git
cd CampusConnect

# 2. Create your environment file and set credentials
cp .env.example .env
# Edit .env and set DB_PASSWORD and MYSQL_ROOT_PASSWORD

# 3. Build and start both containers (app + MySQL)
docker compose up --build
```

The application will be available at `http://localhost:8080`.

**What Docker sets up automatically:**
- MySQL 8.0 container with the `campusConnect` database
- Spring Boot app compiled via multi-stage build, waits for MySQL health check before starting
- Flyway runs `V1__init_schema.sql` then `V2__seed_data.sql` on first boot — no manual SQL step needed
- `/actuator/health` is the Docker health check endpoint

```bash
# Stop containers
docker compose down

# Stop and delete all data (fresh start)
docker compose down -v
```

---

### Option 2: Manual Setup (No Docker)

**Prerequisites**

- Java Development Kit 17
- MySQL Server 8.0+
- Maven (or use the included `./mvnw` wrapper)
- IntelliJ IDEA (recommended) or any IDE

**Steps**

`setup.sh` automates the entire manual setup — checking dependencies, creating `.env`, initialising the database, and starting the application.

```bash
# 1. Clone the repository
git clone https://github.com/subodhadhikari2023/CampusConnect.git
cd CampusConnect

# 2. Run the setup script (handles everything automatically)
chmod +x setup.sh && ./setup.sh
```

On first run, `setup.sh` will:
1. Verify Java 17+, Maven, and MySQL client are installed
2. Create `.env` if it does not exist, then exit — fill in `DB_PASSWORD` and re-run
3. Check whether the database exists — if not, prompt for MySQL root password and run the schema script
4. Start the application via `./mvnw spring-boot:run`

The application will be available at `http://localhost:8080`.

**Dummy credentials** (all use password `password`):

| Username | Role    |
|----------|---------|
| admin1   | Admin   |
| admin2   | Admin   |
| hod1     | HOD     |
| hod2     | HOD     |
| teacher1 | Teacher |
| teacher2 | Teacher |
| student1 | Student |
| student2 | Student |

---

### Option 3: Railway (Cloud)

The application is continuously deployed to Railway from the `main` branch. No local setup is needed to view the live version.

**How Railway deployment works:**

1. A push to `main` triggers the GitHub Actions CI pipeline.
2. CI runs all tests, then builds and pushes a Docker image to GHCR tagged `:latest` and `:<sha>`.
3. Railway is configured to watch GHCR for new images and redeploys automatically.
4. On startup, Flyway applies any pending migrations against the Railway-hosted MySQL instance.

**To deploy your own fork to Railway:**

1. Fork the repository and create a Railway project.
2. Add a **MySQL** plugin to your Railway project — Railway provides `MYSQLHOST`, `MYSQLPORT`, `MYSQLDATABASE`, `MYSQLUSER`, and `MYSQL_ROOT_PASSWORD` automatically.
3. In your CampusConnect Railway service, set these variables explicitly (the app reads them from the environment):

   | Variable           | Value                   |
   |--------------------|-------------------------|
   | `MYSQL_HOST`       | `mysql.railway.internal`|
   | `MYSQL_PORT`       | `3306`                  |
   | `MYSQL_USER`       | `root`                  |
   | `UPLOAD_DIR`       | `/uploads/`             |
   | `SERVER_PORT`      | `8080`                  |

   `MYSQL_ROOT_PASSWORD` and `MYSQL_DATABASE` are injected automatically by the Railway MySQL plugin.

4. Connect your GitHub repository to the Railway service and enable **GitHub Actions → GHCR** deployment (see CI/CD section below).

---

## Environment Variables

The application reads configuration from environment variables with a layered fallback chain. Each environment resolves differently:

| Variable              | Docker Compose        | Railway (auto)                    | Local Dev (default)    |
|-----------------------|-----------------------|-----------------------------------|------------------------|
| `DB_URL`              | `jdbc:mysql://db:3306/campusConnect` | *(not set — Railway path used)* | *(not set — localhost fallback)* |
| `DB_USERNAME`         | `campusConnect`       | *(falls through to `MYSQL_USER`)* | `campusConnect`        |
| `DB_PASSWORD`         | set via `.env`        | *(falls through to `MYSQL_ROOT_PASSWORD`)* | `password`  |
| `MYSQL_HOST`          | *(not needed)*        | `mysql.railway.internal`          | `localhost`            |
| `MYSQL_PORT`          | *(not needed)*        | `3306`                            | `3306`                 |
| `MYSQL_DATABASE`      | *(not needed)*        | `railway` *(set by plugin)*       | `campusConnect`        |
| `MYSQL_USER`          | *(not needed)*        | `root`                            | `campusConnect`        |
| `MYSQL_ROOT_PASSWORD` | set via `.env`        | set by Railway MySQL plugin       | `password`             |
| `UPLOAD_DIR`          | `/uploads/`           | `/uploads/`                       | `uploads/`             |

**Fallback chain in `application.properties`:**

```
DB_URL           → MYSQL_HOST:MYSQL_PORT/MYSQL_DATABASE → localhost:3306/campusConnect
DB_USERNAME      → MYSQL_USER → campusConnect
DB_PASSWORD      → MYSQL_ROOT_PASSWORD → password
```

`DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` take highest priority and are always set by Docker Compose. When they are absent (Railway and local dev), the app falls back to the Railway-native `MYSQL_*` variables, and finally to hardcoded localhost defaults for local development.

**Important:** Spring Boot's `${VAR:fallback}` syntax only activates the fallback when the variable is **undefined**. An empty string is accepted as-is and will cause a broken connection URL. Always verify actual variable values with `railway variables --service <name>` rather than assuming the Railway dashboard shows the effective value.

Copy `.env.example` to `.env` for Docker or local use:

```bash
cp .env.example .env
```

`.env` is git-ignored and must never be committed.

---

## Development Workflow

All feature work follows a **branch → PR → review → merge** cycle. Direct commits to `main` are not permitted.

```
main        ──────────────────────────────────────────► production
               ▲                   ▲
               │ PR merge          │ PR merge
dev         ───┼────────────────────┼──────────────────►
               │                   │
feature/*   ───┘                   │
                                   │
bugfix/*    ───────────────────────┘
```

### Branch Strategy

| Branch prefix | Purpose |
|---------------|---------|
| `main`        | Production-ready code; every commit triggers Railway deploy |
| `dev`         | Integration branch; all feature PRs target `dev` first |
| `feature/*`   | New features — branch from `dev`, PR back to `dev` |
| `bugfix/*`    | Bug fixes — branch from `dev` (or `main` for hotfixes) |

### Step-by-step

```bash
# 1. Start from an up-to-date dev branch
git checkout dev && git pull origin dev

# 2. Create a feature branch
git checkout -b feature/your-feature-name

# 3. Develop with TDD — write tests first, then implementation
mvn test   # must be green before committing

# 4. Commit with a descriptive message
git add <files>
git commit -m "feat: describe what this adds and why"

# 5. Push and open a PR targeting dev
git push origin feature/your-feature-name
# Open PR: feature/your-feature-name → dev

# 6. After review and merge into dev, open a PR: dev → main
# CI runs automatically; Railway deploys on merge to main
```

### CI/CD Pipeline

Every push and pull request triggers GitHub Actions:

```
Push to any branch
        │
        ▼
  ┌─────────────┐
  │  test job   │  mvn test (194 tests, H2 in-memory)
  └──────┬──────┘
         │ pass
         ▼
  Is branch == main?
         │
    Yes  │
         ▼
  ┌──────────────────┐
  │  push-image job  │  docker build → push to GHCR
  │                  │  tags: :latest + :<sha>
  └──────┬───────────┘
         │
         ▼
  Railway watches GHCR
  → auto-redeploys
  → Flyway applies migrations
  → /actuator/health confirms startup
```

The test job runs on **all branches and all pull requests**. The image-push job runs **only on merges to `main`**.

---

## Testing

The project includes **194 tests** covering all application layers.

| Layer | Tests | Framework | Notes |
|---|---|---|---|
| Controllers | 136 | `@WebMvcTest` + Mockito | Admin (42), HOD (41), Teacher (32), Student (17), Master (4) — views, model attributes, redirects, flash messages, ownership guards |
| Services | 33 | JUnit 5 + Mockito | UserService — pure unit tests, no Spring context |
| Repositories | 14 | `@DataJpaTest` + H2 | FileDAO, RoleDAO, UserDAO — custom JPQL queries and derived finders |
| Integration | 1 | `@SpringBootTest` | Full context smoke test |

Tests use an **H2 in-memory database** via `application-test.properties` with `spring.flyway.enabled=false` — no MySQL or schema files needed in CI.

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=HodControllerTest

# Run tests matching a pattern
mvn test -Dtest="*ControllerTest"
```

---

## CI/CD Pipeline

The GitHub Actions workflow file is at `.github/workflows/ci.yml`.

### Jobs

**`test` — Build & Test**
- Triggers: every push to any branch; every pull request targeting `main`
- Environment: `ubuntu-latest`, Java 17 Temurin, Maven cache enabled
- Runs: `mvn test` with `SPRING_PROFILES_ACTIVE=test` (H2 in-memory, Flyway disabled)
- No MySQL service required — all 194 tests run against H2

**`push-image` — Docker Build & Push to GHCR**
- Triggers: push to `main` only, after `test` passes
- Logs in to `ghcr.io` using `GITHUB_TOKEN` (no secret configuration needed)
- Builds the multi-stage Dockerfile: Maven build stage → JRE 17 Alpine runtime
- Pushes two tags:
  - `ghcr.io/subodhadhikari2023/campusconnect:latest`
  - `ghcr.io/subodhadhikari2023/campusconnect:<commit-sha>`

Pull the latest image:

```bash
docker pull ghcr.io/subodhadhikari2023/campusconnect:latest
```

### Railway Auto-Deploy

Railway is connected to the GHCR repository. When a new `:latest` image is pushed, Railway:

1. Pulls the new image
2. Starts the container with environment variables injected
3. Spring Boot starts → Flyway checks `flyway_schema_history` → applies any new `V{n}` migrations
4. `/actuator/health` is polled; traffic is routed only after the health check passes

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for the full contribution guide, including branch naming, TDD requirements, Javadoc rules, and commit message conventions.

**Quick reference:**

1. Fork the repository
2. Branch from `dev`: `git checkout -b feature/your-feature-name`
3. Write tests first, then implementation (TDD — red → green → commit)
4. Add Javadoc to all `public` and `protected` members
5. Commit: `git commit -m 'feat: describe the change'`
6. Push and open a PR targeting `dev`

Contributions are welcome. Please open an issue first for significant changes.

---

## License

Distributed under the GNU General Public License. See [LICENSE](LICENSE) for details.

---

## Contact

**Subodh Adhikari**
<a href="mailto:subodhadhikari2023@outlook.com"><img src="https://img.shields.io/badge/Outlook-blue?style=flat-square&logo=microsoft-outlook" /></a>
<a href="https://www.linkedin.com/in/subodh-adhikari-4b811a296/"><img src="https://img.shields.io/badge/LinkedIn-blue?style=flat-square&logo=linkedin" /></a>

**Manav Agarwal**
<a href="mailto:manav9981@outlook.com"><img src="https://img.shields.io/badge/Outlook-blue?style=flat-square&logo=microsoft-outlook" /></a>
<a href="https://www.linkedin.com/in/manav-agarwal-8139b92b8/"><img src="https://img.shields.io/badge/LinkedIn-blue?style=flat-square&logo=linkedin" /></a>

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

[contributors-shield]: https://img.shields.io/github/contributors/subodhadhikari2023/CampusConnect?style=for-the-badge
[contributors-url]: https://github.com/subodhadhikari2023/CampusConnect/graphs/contributors
[issues-shield]: https://img.shields.io/github/issues/subodhadhikari2023/CampusConnect?style=for-the-badge
[issues-url]: https://github.com/subodhadhikari2023/CampusConnect/issues
[license-shield]: https://img.shields.io/badge/License-GPLv3-blue.svg
[license-url]: https://github.com/subodhadhikari2023/CampusConnect/blob/main/LICENSE
[ci-shield]: https://github.com/subodhadhikari2023/CampusConnect/actions/workflows/ci.yml/badge.svg
[ci-url]: https://github.com/subodhadhikari2023/CampusConnect/actions
[docker-shield]: https://img.shields.io/badge/Docker-available-2496ED?style=flat-square&logo=docker&logoColor=white
[docker-url]: https://github.com/subodhadhikari2023/CampusConnect/pkgs/container/campusconnect

[HTML.js]: https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white
[HTML-url]: https://html.com/
[CSS.js]: https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white
[CSS-url]: https://css-tricks.com/
[Javascript.js]: https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black
[Javascript-url]: https://developer.mozilla.org/en-US/docs/Web/JavaScript
[Thymeleaf.js]: https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white
[Thymeleaf-url]: https://www.thymeleaf.org/
[SpringBoot6.js]: https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white
[SpringBoot6-url]: https://spring.io/projects/spring-boot
[SpringSecurity.js]: https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[SpringSecurity-url]: https://spring.io/projects/spring-security
[MySQL.js]: https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white
[MySQL-url]: https://www.mysql.com/
[JPA.com]: https://img.shields.io/badge/Hibernate_JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white
[JPA-url]: https://www.baeldung.com/learn-jpa-hibernate
[DockerBadge.js]: https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white
[Docker-url]: https://www.docker.com/
[GHA.js]: https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white
[GHA-url]: https://github.com/features/actions
[GHCR.js]: https://img.shields.io/badge/GHCR-181717?style=for-the-badge&logo=github&logoColor=white
[GHCR-url]: https://github.com/subodhadhikari2023/CampusConnect/pkgs/container/campusconnect
[JUnit.js]: https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white
[JUnit-url]: https://junit.org/junit5/
[Mockito.js]: https://img.shields.io/badge/Mockito-78A641?style=for-the-badge
[Mockito-url]: https://site.mockito.org/
[H2.js]: https://img.shields.io/badge/H2_Database-1021FF?style=for-the-badge
[H2-url]: https://www.h2database.com/
[SST.js]: https://img.shields.io/badge/Spring_Security_Test-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[SST-url]: https://docs.spring.io/spring-security/reference/servlet/test/index.html
[Flyway.js]: https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white
[Flyway-url]: https://flywaydb.org/
[Actuator.js]: https://img.shields.io/badge/Spring_Actuator-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[Actuator-url]: https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html
