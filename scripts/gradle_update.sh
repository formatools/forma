#!/usr/bin/env bash
set -euo pipefail

function usage {
  echo "Usage: $0 <gradle_version>"
  exit 1
}

function check_command {
  if ! command -v "$1" &>/dev/null; then
    echo "Error: $1 is not installed."
    exit 1
  fi
}

# check if current dir contains .github
if [ ! -d ".github" ]; then
  echo "This script must be run from the root of the repository."
  exit 1
fi

# read gradle version from script arg
if [ -z "${1:-}" ]; then
  usage
fi

gradle_version="$1"

# check if required commands are available
check_command find
check_command dirname

# update gradle version in all folders containing gradlew

# find all gradlew files recursively
gradlew_files=$(find . -name gradlew)

# loop through gradlew files
for gradlew_file in $gradlew_files; do
  # get directory containing gradlew file
  gradlew_dir=$(dirname "$gradlew_file")

  # update gradle wrapper version
  echo "Updating gradle wrapper version in $gradlew_dir"
  cd "$gradlew_dir" || exit 1
  ./gradlew wrapper --gradle-version "$gradle_version"
  cd - || exit 1
done
