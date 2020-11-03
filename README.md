# forma (WIP)
<img src="./press.svg" width="150" height="150">

Forma - Meta Build System with Android and Gradle support. Opinionated, scalable, thoughtfully structured, type-safe and guided way to declare your project structure. Distributed as a Gradle plugin, Forma helps developers to shift focus from `Build Configuration` to `Project Structure Declaration`, abstracting away build configuration complexity.

- You don't need to be a gradle expert anymore
- Get rid of project configuration bad practices
- Type-safe, single method configuration for your targets, no room for error
- Built-in dependency visibility rules
- Target types - enforce scalable project structure
- High-performance builds: Gradle best practices are applied automatically
- Opinionated dependency framework - helps developers to understand and deal with transitive dependencies hell
- Extensible - be the expert when you need to!
- And much more...

⚠️ We are using `target` term to express application components(e.g. modules or projects, depending in the context) across documentation and code, there is couple of reasons for that. `Module` term often confused with Dagger modules which makes communication harder, `project` from the other hand used only in Gradle context but not in other build systems like Buck and Bazel.

Easiest way to start is here >> ‼️ https://github.com/stepango/FormaShowcase ‼️

Configuration made easy:

```kotlin
// root build.gradle.kts

// Configure shared aspects of your android Projects in a single place,
// no need to copy it over to all your targets or invent some smart sharing technique;)
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
// Type-safe creation of your target is a single method call
// Required plugins applied automatically
// Configuration inferred from Forma.configure
// Configuration is fast ;)
androidLibrary(
    // Mandatory, visible from build configuration
    packageName = "com.stepango.example",
    // External dependencies declaration, one universal syntax
    dependencies = deps(
        google.material,
        androidx.appcompat,
        androidx.constraintlayout,
        androidx.navigation,
        androidx.vectordrawable
    ) + deps(
        // Internal project dependencies, declared separately from externals
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

|    Supported target types     | implemented |
|:-----------------------------:|:-----------:|
|         androidBinary         |      ✅      |
|         androidLibrary        |      ✅      |
|       androidDataBinding      |      ✅      |
|   androidDataBindingAdapters  |      ✅      |
|         androidWidget         |      ✅      |
|           androidRes          | in progress  |
|        androidTestUtils       |      ✅     |
|          androidUtils         |      ✅     |
|           testUtils           |      ✅      |
|             utils             |      ✅      |
|             api               |      ✅      |
|             impl              |      ✅      |



<div>Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>


