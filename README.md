# forma (WIP)
<img src="./press.svg" width="150" height="150">

Opinionated, scalable, thoughtfully structured, type-safe, guided  Gradle project framework for Android.

- You don't need to be a gradle expert anymore
- Get rid of project configuration bad practices
- Type-safe, single method configuration for your modules, no room for error
- Built-in dependency visibility rules
- Module types - enforce scalable project structure
- High-performance builds: Gradle best practices are applied automatically
- Opinionated dependency framework - helps developers to understand and deal with transitive dependencies hell
- Extensible - be the expert when you need to!
- And much more...

Poject configuration in a single method call:

```kotlin
// root build.gradle.kts
Forma.configure(
    minSdk = 21,
    targetSdk = 29,
    compileSdk = 29,
    kotlinVersion = "1.4.0",
    agpVersion = "4.0.0",
    versionCode = 1,
    versionName = "1.0",
    repositories = {
        google()
        jcenter()
    }
)
```

Your kotlin android library

```kotlin
android_library(
    packageName = "com.stepango.example",
    dependencies = deps(
        google.material,
        androidx.core_ktx,
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.navigation_fragment_ktx,
        androidx.navigation_ui_ktx,
        androidx.vectordrawable
    )
)
```

<div>Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>


