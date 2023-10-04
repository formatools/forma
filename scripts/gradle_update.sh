#!/usr/bin/env bash

# Usage example: ./scripts/gradle_update.sh 8.3

# check if current dir contains .github
if [ ! -d ".github" ]; then
  echo "This script must be run from the root of the repository."
  exit 1
fi

# read gradle version from script arg
if [ -z "$1" ]; then
  echo "Please provide a gradle version."
  exit 1
fi

gradle_version=$1

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
