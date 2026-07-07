#!/usr/bin/env bash
# Checks GHCR for a new image digest; if changed, pulls and redeploys.
# Intended to run via cron on the production VM (not in CI, not on dev machines).
set -euo pipefail

cd "$(dirname "$0")/.."  # repo root, assuming this script lives in deploy/

IMAGE="ghcr.io/subodhadhikari2023/campusconnect:latest"
LOG_PREFIX="[$(date '+%Y-%m-%d %H:%M:%S')]"

old_digest=$(docker inspect --format='{{index .RepoDigests 0}}' "$IMAGE" 2>/dev/null || echo "none")

docker compose pull app >/dev/null 2>&1

new_digest=$(docker inspect --format='{{index .RepoDigests 0}}' "$IMAGE" 2>/dev/null || echo "none")

if [ "$old_digest" != "$new_digest" ]; then
    echo "$LOG_PREFIX New image detected ($old_digest -> $new_digest). Redeploying..."
    docker compose up -d app
    docker image prune -f >/dev/null 2>&1
    echo "$LOG_PREFIX Redeploy complete."
else
    echo "$LOG_PREFIX No change (digest: $new_digest). Skipping redeploy."
fi
