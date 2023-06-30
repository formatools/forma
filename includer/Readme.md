# Includer

Simple and powerful plugin which helps you to save time and avoid writing tons of boilerplate code. 
Uses kotlin coroutines to efficiently traverse even deeply nested project trees.

## Installation

Remove all your `include` declarations from your `settings.gradle.kts` file and replace them with:

```kotlin
plugins {
    id("tools.forma.includer") version "0.1.3"
}
```

# How does it work?

Includer traverses the project's file tree and includes as subprojects all directories that have 
files with `.gradle(.kts)` extensions. The plugin looks for more than just `build.gradle(.kts)` by default. 
Because in large projects it may be necessary to use different build file names to make it 
easier to search for them by name.

Includer skips directories that have `settings.gradle(.kts)` files, treating them as nested projects.

Example:

```
rootProject
|--app <- will be included as :app
|----build.gradle.kts
|
|--build-logic <- will be ignored
|----settings.gradle.kts
|
|--feature1
|----api <- will be included as :feature1-api
|------api.gradle.kts
```

# Plugin configuration

The plugin is configurable by specifying properties in the `gradle.properties` file.

## Ignored folders

Includer always skips directories with following names: `build`, `src`, `buildSrc`. But you can 
specify additional ignored folder names:

```properties
tools.forma.includer.ignoredFolders=.cmake_cache,.gradle,gradle
```

## Arbitrary build file names

If in your case arbitrary build file names interfere with proper project configuration, 
you can disable this option:

```properties
tools.forma.includer.arbitraryBuildFileNames=false
```
