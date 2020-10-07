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

Easiest way to start is here >> ‼️ https://github.com/stepango/FormaShowcase ‼️

Configuration made easy:

```kotlin
// root build.gradle.kts

// Configure shared aspects of your android Projects in a single place,
// no need to copy it over to all your modules or invent some smart sharing technique;)
androidProjectConfiguration(
    minSdk = 21,
    targetSdk = 29,
    compileSdk = 29,
    kotlinVersion = "1.4.10",
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
// Type-safe creation of your module is a single method call
// Required plugins applied automatically
// Configuration inferred from Forma.configure
// Configuration is fast ;)
androidLibrary(
    // Mandatory, visible from build configuration
    packageName = "com.stepango.example",
    // External dependencies declaration, one universal syntax
    dependencies = deps(
        google.material,
        androidx.core_ktx,
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.navigation_fragment_ktx,
        androidx.navigation_ui_ktx,
        androidx.vectordrawable
    ),
    // Internal project dependencies, declared separately from externals
    projectDependencies = deps(
        project(":demo-library")
    ),
    // Test dependencies declaration
    testDependencies = deps(
        test.junit
    ),
    // Android test dependencies declaration
    androidTestDependencies = deps(
        test.junit_ext,
        test.espresso
    )
)
```

|    Supported module types     | implemented |    status    |
|:-----------------------------:|:-----------:|:------------:|
|         androidBinary         |      ✅      |              |
| androidLibrary/androidFeature | in progress | experimental |
|       androidDataBinding      | in progress |              |
|   androidDataBindingAdapters  | in progress |              |
|         androidWidget         | in progress |              |
|           androidRes          | planned     |              |
|        androidTestUtils       |      ✅     |              |
|          androidUtils         | planned     |              |
|           testUtils           |      ✅      |              |
|             utils             |      ✅      |              |
|             api               |      ✅      |              |
|             impl              |      ✅      |              |

<div>Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>


