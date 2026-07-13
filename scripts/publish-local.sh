#!/usr/bin/env bash
# Publish Forma plugins + forma-core to the local Maven cache (~/.m2) for testing.
#
# Usage:
#   ./scripts/publish-local.sh
#   ./scripts/publish-local.sh 0.1.3-LOCAL     # version override (recommended for experiments)
#
# Then consume from another project WITHOUT includeBuild:
#   pluginManagement {
#     repositories { mavenLocal(); google(); mavenCentral(); gradlePluginPortal() }
#   }
#   plugins { id("tools.forma.android") version "0.1.3-LOCAL" }
#
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
# shellcheck disable=SC1091
[[ -f "$ROOT/scripts/env-mac.sh" ]] && source "$ROOT/scripts/env-mac.sh" || true

VERSION_ARG="${1:-}"
EXTRA=()
if [[ -n "$VERSION_ARG" ]]; then
  EXTRA+=("-PformaLocalVersion=$VERSION_ARG")
fi

cd "$ROOT/plugins"
echo "=== publishAllToMavenLocal ${VERSION_ARG:+(version $VERSION_ARG)} ==="
./gradlew publishAllToMavenLocal "${EXTRA[@]}" --console=plain

# Resolve version used
VER="${VERSION_ARG:-0.1.3}"
echo
echo "=== installed under ~/.m2 (tools/forma) ==="
find "${HOME}/.m2/repository/tools/forma" -maxdepth 3 -type d 2>/dev/null | head -40 || true
echo
ls -la "${HOME}/.m2/repository/tools/forma/core/${VER}/" 2>/dev/null || \
  ls -la "${HOME}/.m2/repository/tools/forma/" 2>/dev/null | head || true
echo
echo "Done. Consumer version: $VER"
echo "Plugin id: tools.forma.android"
