[![Contributors][contributors-shield]][contributors-url]
[![Issues][issues-shield]][issues-url]
[![GNU License][license-shield]][license-url]
[![CI][ci-shield]][ci-url]
[![Docker][docker-shield]][docker-url]

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
      </ul>
    </li>
    <li><a href="#testing">Testing</a></li>
    <li><a href="#ci-cd">CI/CD</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

---

## About The Project

**Campus Connect** is a role-based academic management and collaborative learning platform designed for educational institutions. It provides a centralized hub where administrators, department heads, faculty, and students can manage academic workflows, share resources, and collaborate — all within a structured, permission-driven environment.

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

# 2. Create your environment file and fill in your credentials
cp .env.example .env
# Open .env and set DB_PASSWORD and MYSQL_ROOT_PASSWORD

# 3. Build and start both containers (app + MySQL)
docker compose up --build
```

The application will be available at `http://localhost:8080`.

**What Docker sets up automatically:**
- MySQL 8.0 container — creates the `campusConnect` database from your `.env`
- Spring Boot app — compiled via multi-stage build, waits for MySQL health check before starting
- Flyway runs `V1__init_schema.sql` then `V2__seed_data.sql` on first boot — no manual SQL step needed
- `/actuator/health` is the Docker health check endpoint

```bash
# Stop the containers
docker compose down

# Stop and delete all data (fresh start)
docker compose down -v
```

---

### Option 2: Manual Setup (No Docker)

Use this if you prefer to run the application directly on your machine without containers.

**Prerequisites**

- Java Development Kit 17
- MySQL Server 8.0+
- Maven (or use the included `./mvnw` wrapper)
- IntelliJ IDEA (recommended) or any IDE

**Steps**

`setup.sh` automates the entire manual setup — checking dependencies, creating the `.env`, initialising the database, and starting the application.

```bash
# 1. Clone the repository
git clone https://github.com/subodhadhikari2023/CampusConnect.git
cd CampusConnect

# 2. Run the setup script (handles everything below automatically)
chmod +x setup.sh && ./setup.sh
```

On first run, `setup.sh` will:
1. Verify Java 17+, Maven, and MySQL client are installed
2. Create `Project/.env` if it does not exist, then exit — fill in `DB_PASSWORD` and re-run
3. Check whether the database exists — if not, prompt for MySQL root password and run the schema script
4. Start the application via `./mvnw spring-boot:run`

The application will be available at `http://localhost:8080`.

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
```

---

## CI/CD

Every push and pull request targeting `main` triggers the GitHub Actions pipeline:

1. **Test job** — Runs the full 194-test suite against H2 in-memory DB (no MySQL required)
2. **Push job** *(main branch only)* — Builds the Docker image and pushes to GitHub Container Registry (GHCR) tagged with `:latest` and the commit SHA
3. **Railway** — Automatically redeploys on every push to `main`; Flyway applies any new migrations on startup

Pull the latest image:

```bash
docker pull ghcr.io/subodhadhikari2023/campusconnect:latest
```

---

## Contributing

1. Fork the repository
2. Clone your fork: `git clone https://github.com/your_username/CampusConnect.git`
3. Create a feature branch: `git checkout -b feature/your-feature-name`
4. Make your changes and stage them: `git add <files>`
5. Commit: `git commit -m 'Add your feature description'`
6. Push: `git push origin feature/your-feature-name`
7. Open a Pull Request against `main`

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
