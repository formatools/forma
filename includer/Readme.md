# Includer

Simple and powerful plugin which helps you to save time and avoid writing tons of boilerplate code. Uses kotlin coroutines to efficiently traverse even deeply nested project trees.

## Installation

Remove all your `include` declarations from your `settings.gradle.kts` file and replace them with:

```kotlin
plugins {
    id("tools.forma.includer") version "0.1.3"
}
```
