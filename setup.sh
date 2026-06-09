#!/usr/bin/env bash
# setup.sh — Bootstrap CampusConnect on a new machine and start the application.
#
# What it does:
#   1. Verifies required tools are installed (Java 17+, Maven, MySQL client)
#   2. Creates .env from .env.example if it doesn't exist yet
#   3. Loads environment variables from .env
#   4. Validates that required env vars are set
#   5. Checks whether the campusConnect database already exists
#   6. If not, asks for the MySQL root password and runs the full schema + seed script
#   7. Starts the Spring Boot application
#
# Usage:
#   chmod +x setup.sh
#   ./setup.sh

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$REPO_ROOT/.env"
SQL_SCRIPT="$REPO_ROOT/sql-scripts/databaseScript.sql"

# ── Colours ─────────────────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
info()    { echo -e "${CYAN}[INFO]${NC}  $*"; }
success() { echo -e "${GREEN}[OK]${NC}    $*"; }
warn()    { echo -e "${YELLOW}[WARN]${NC}  $*"; }
error()   { echo -e "${RED}[ERROR]${NC} $*" >&2; exit 1; }

echo ""
echo "╔══════════════════════════════════════════╗"
echo "║       CampusConnect — Setup & Run        ║"
echo "╚══════════════════════════════════════════╝"
echo ""

# ── 1. Check Java ────────────────────────────────────────────────────────────
info "Checking Java..."
if ! command -v java &>/dev/null; then
    error "Java not found. Install JDK 17 or later and make sure 'java' is on your PATH."
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
# Handle versions like "1.8" (Java 8)
[[ "$JAVA_VERSION" == "1" ]] && JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f2)

if [[ "$JAVA_VERSION" -lt 17 ]]; then
    error "Java 17+ is required. Found Java $JAVA_VERSION."
fi
success "Java $JAVA_VERSION"

# ── 2. Check Maven ───────────────────────────────────────────────────────────
info "Checking Maven..."
MVN_CMD=""
if [[ -f "$REPO_ROOT/mvnw" ]]; then
    chmod +x "$REPO_ROOT/mvnw"
    MVN_CMD="$REPO_ROOT/mvnw"
    success "Using Maven Wrapper (mvnw)"
elif command -v mvn &>/dev/null; then
    MVN_CMD="mvn"
    success "Using system Maven: $(mvn --version | head -1)"
else
    error "Maven not found. Install Maven or ensure the mvnw wrapper exists at the repo root."
fi

# ── 3. Check MySQL client ────────────────────────────────────────────────────
info "Checking MySQL client..."
if ! command -v mysql &>/dev/null; then
    error "MySQL client ('mysql') not found. Install MySQL Server 8.0+ which includes the client."
fi
success "MySQL client found"

# ── 4. Set up .env ───────────────────────────────────────────────────────────
info "Checking environment file..."
if [[ ! -f "$ENV_FILE" ]]; then
    warn ".env not found — creating a blank one at $REPO_ROOT/.env"
    cat > "$ENV_FILE" <<'EOF'
DB_URL=jdbc:mysql://localhost:3306/campusConnect
DB_USERNAME=campusConnect
DB_PASSWORD=
UPLOAD_DIR=uploads/
EOF
    warn "Fill in DB_PASSWORD (and adjust other values if needed), then re-run this script."
    echo ""
    echo "  $ENV_FILE"
    echo ""
    exit 1
fi
success ".env found at $ENV_FILE"

# ── 5. Load env vars ─────────────────────────────────────────────────────────
info "Loading environment variables..."
# Export each non-comment, non-empty line
set -o allexport
# shellcheck disable=SC1090
source "$ENV_FILE"
set +o allexport
success "Environment variables loaded"

# ── 6. Validate required vars ────────────────────────────────────────────────
info "Validating required variables..."
MISSING=()
[[ -z "${DB_PASSWORD:-}" ]] && MISSING+=("DB_PASSWORD")
[[ -z "${DB_URL:-}"      ]] && MISSING+=("DB_URL")
[[ -z "${DB_USERNAME:-}" ]] && MISSING+=("DB_USERNAME")

if [[ ${#MISSING[@]} -gt 0 ]]; then
    error "These required variables are not set in $ENV_FILE: ${MISSING[*]}"
fi

# Parse host and port from DB_URL (jdbc:mysql://host:port/dbname)
DB_HOST=$(echo "$DB_URL" | sed -E 's|jdbc:mysql://([^:/]+).*|\1|')
DB_PORT=$(echo "$DB_URL" | sed -E 's|jdbc:mysql://[^:]+:([0-9]+).*|\1|')
DB_PORT="${DB_PORT:-3306}"
DB_NAME=$(echo "$DB_URL" | sed -E 's|.*:([0-9]+)/([^?]+).*|\2|; s|.*/([^?]+)|\1|')

success "DB target: $DB_HOST:$DB_PORT/$DB_NAME as $DB_USERNAME"

# ── 7. Check / create database ───────────────────────────────────────────────
info "Checking if database '$DB_NAME' exists..."

DB_EXISTS=$(mysql -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USERNAME" -p"$DB_PASSWORD" \
    --batch --skip-column-names \
    -e "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME='$DB_NAME';" 2>/dev/null || true)

if [[ -n "$DB_EXISTS" ]]; then
    success "Database '$DB_NAME' already exists — skipping setup"
else
    warn "Database '$DB_NAME' not found. Running setup script..."
    echo ""
    echo "  The setup script requires MySQL root access to create the database and user."
    echo -n "  Enter MySQL root password: "
    read -rs MYSQL_ROOT_PASS
    echo ""

    if ! mysql -h "$DB_HOST" -P "$DB_PORT" -u root -p"$MYSQL_ROOT_PASS" \
        < "$SQL_SCRIPT" 2>&1; then
        error "Database setup failed. Check your root password and that MySQL is running."
    fi
    success "Database '$DB_NAME' created and seeded"
fi

# ── 8. Start the application ─────────────────────────────────────────────────
echo ""
info "Starting CampusConnect on http://localhost:8080 ..."
info "Press Ctrl+C to stop."
echo ""

cd "$REPO_ROOT"
exec $MVN_CMD spring-boot:run
