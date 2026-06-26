# Contributing to CampusConnect

Thank you for taking the time to contribute. The guidelines below keep the codebase consistent and the review process predictable.

---

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Branch Strategy](#branch-strategy)
- [Development Process](#development-process)
- [Commit Messages](#commit-messages)
- [Javadoc Requirements](#javadoc-requirements)
- [Testing Requirements](#testing-requirements)
- [Pull Request Process](#pull-request-process)
- [Reporting Issues](#reporting-issues)

---

## Code of Conduct

Be respectful and constructive in all interactions. Treat every contributor as a collaborator, not a critic.

---

## Getting Started

1. **Fork** the repository on GitHub.
2. **Clone** your fork locally:
   ```bash
   git clone https://github.com/<your-username>/CampusConnect.git
   cd CampusConnect
   ```
3. **Set up** the development environment using Docker or the manual setup described in [README.md](README.md).
4. Verify the test suite is green before making any changes:
   ```bash
   mvn test
   ```

---

## Branch Strategy

`main` is the only protected branch — it always reflects production. **Never commit directly to `main`.** All changes reach `main` via a Pull Request.

| Branch       | Purpose |
|--------------|---------|
| `main`       | Production — Railway deploys from here; PR only, no direct commits |
| `feature/*`  | Isolated new features |
| `bugfix/*`   | Bug fixes |
| `hotfix/*`   | Critical production patches |
| `docs/*`     | Documentation-only changes |
| `refactor/*` | Refactoring with no behaviour change |

Every branch is equal — any branch can open a PR directly against `main`.

```bash
# Branch from main
git checkout main && git pull origin main
git checkout -b feature/your-feature-name
```

---

## Development Process

This project follows **strict Test-Driven Development**:

1. **Red** — Write a failing test that describes the desired behaviour.
2. **Green** — Write the minimum implementation to make the test pass.
3. **Commit** — Stage and commit only after all tests are green.

Never commit a feature that does not have a corresponding passing test. The pre-commit hook checks for Javadoc coverage; the CI pipeline blocks merges when tests fail.

```bash
# Verify all tests pass before every commit
mvn test

# Run a single test class during development
mvn test -Dtest=HodControllerTest
```

---

## Commit Messages

Use the conventional commits format:

```
<type>: <short summary in imperative mood>
```

| Type       | When to use |
|------------|-------------|
| `feat`     | New feature or capability |
| `fix`      | Bug fix |
| `refactor` | Code change with no behaviour change |
| `test`     | Adding or correcting tests |
| `docs`     | Documentation only |
| `config`   | Build, CI, or configuration changes |
| `chore`    | Dependency updates, tooling |

**Rules:**
- Summary line is 72 characters maximum
- Imperative mood: "add user export" not "added user export"
- No trailing period on the summary line
- No AI attribution trailers (no "Co-Authored-By: Claude" etc.)

**Examples:**
```
feat: add GZIP download for resource files
fix: prevent HOD from viewing other department courses
test: cover ownership guard in TeacherController delete endpoint
docs: update README with Railway deployment instructions
config: add Flyway migration V3 for announcement timestamps
```

---

## Javadoc Requirements

Every `public` and `protected` class, interface, method, and field must have a Javadoc comment before a PR can be merged. This is enforced by:

- A `PostToolUse` Claude Code hook that warns after every `.java` edit in development sessions
- A `pre-commit` git hook that blocks the commit if staged files have undocumented public/protected members

**Rules:**
- First sentence is the summary — one line only, ends with a period
- Use `@param`, `@return`, and `@throws` only when they add information beyond what the signature already conveys
- Do not restate the method name in prose ("Returns the user" for `getUser()` adds nothing)

```java
/**
 * Uploads a file to the filesystem and records its metadata in the database.
 *
 * @param dto carries the multipart file, course, semester, subject, and category
 * @throws IOException if the target directory cannot be created or written to
 */
public void uploadToFileSystem(FileUploadDTO dto) throws IOException { ... }
```

---

## Testing Requirements

| Layer | What to test | Framework |
|-------|-------------|-----------|
| Controllers | View names, model attributes, redirects, flash messages, role-based access guards | `@WebMvcTest` + Mockito |
| Services | Business logic, file operations, ownership resolution | JUnit 5 + Mockito |
| Repositories | Custom JPQL queries and derived finders | `@DataJpaTest` + H2 |
| Integration | Full application context smoke test | `@SpringBootTest` |

**Test configuration:**
- Tests use `application-test.properties` which activates H2 in-memory and disables Flyway
- Never mock the database in `@DataJpaTest` — let H2 handle it; `spring.jpa.hibernate.ddl-auto=create-drop` rebuilds the schema per test run
- Do not introduce production code changes just to make tests easier to write — redesign the test instead

---

## Pull Request Process

1. Ensure all tests pass locally: `mvn test`
2. Push your branch: `git push origin feature/your-feature-name`
3. Open a PR targeting **`main`**
4. Fill in the PR template:
   - **What** changed and **why**
   - Test plan — what you tested and how
   - Any screenshots for UI changes
5. At least one review approval is required before merging
6. All CI checks (test job) must be green
7. Squash or rebase as needed to keep history clean before merge

On merge to `main` the `push-image` CI job builds and pushes the Docker image to GHCR, and Railway redeploys automatically.

---

## Reporting Issues

Use the GitHub issue tracker. Fill in the issue template — it includes a user story format with acceptance criteria. Vague issue reports without reproduction steps will be closed.

Before opening an issue:
- Search existing issues to avoid duplicates
- If it is a security vulnerability, do not open a public issue — email the maintainer directly
