# Includer

<a href="https://plugins.gradle.org/plugin/tools.forma.includer"><img src="https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/tools/forma/includer/tools.forma.includer.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=Includer"/></a>

Simple and powerful plugin which helps you to save time and avoid writing tons of boilerplate code. 
Uses kotlin coroutines to efficiently traverse even deeply nested project trees.

## Installation

Remove all your `include` declarations from your `settings.gradle.kts` file and replace them with:

```kotlin
plugins {
    id("tools.forma.includer") version "0.2.0"
}
```

# How does it work?

Includer traverses the project's file tree and includes as subprojects all directories that have 
files with `build.gradle(.kts)` files.

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
|------build.gradle.kts
```

# Plugin configuration

The plugin is configurable by specifying properties in the `includer` extension.

## Ignored folders

Includer always skips directories with following names: `build`, `src`, `buildSrc`. But you can 
specify additional ignored folder names:

```kotlin
// in settings.gradle.kts file after `plugins` block
includer {
    excludeFolders(".cmake_cache", "scripts")
}
```

## Arbitrary build file names

If you want the plugin to look for any `*.gradle(.kts)` files, not just `build.gradle(.kts)`:

```kotlin
// in settings.gradle.kts file after `plugins` block
includer {
    arbitraryBuildScriptNames = true
}
```

> NOTE: If you use this property, there can only be one `*.gradle(.kts)` file in the root 
> of any module.
