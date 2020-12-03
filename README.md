
<p align="center">
    <a href="https://forma.tools" target="_blank" rel="noopener noreferrer"><img width="100" src="./img/press.svg" alt="Logo"></a>
</p>

<p align="center">
    <a href="https://forma.tools"><img src="https://forthebadge.com/images/badges/built-by-developers.svg"/></a>
    <a href="https://forma.tools"><img src="https://forthebadge.com/images/badges/built-for-android.svg"/></a>
    <a href="https://forma.tools"><img src="https://forthebadge.com/images/badges/it-works-why.svg"/></a>
</p>

<p align="center">
    <img src="https://github.com/stepango/forma/workflows/Android%20CI/badge.svg"/>
    <a href="https://plugins.gradle.org/plugin/tools.forma.android"><img src="https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/tools/forma/android/tools.forma.android.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=Gradle%20Plugin"/></a>
    <img alt="GitHub code size in bytes" src="https://img.shields.io/github/languages/code-size/stepango/forma">
    <img alt="License" src="https://img.shields.io/github/license/formatools/forma"/>
    <img alt="Contributors" src="https://img.shields.io/github/contributors/formatools/forma"/>
    <img alt="GitHub top language" src="https://img.shields.io/github/languages/top/formatools/forma"/>
    <img alt="GitHub closed pull requests" src="https://img.shields.io/github/issues-pr-closed/formatools/forma"/>
    <img alt="GitHub Repo stars" src="https://img.shields.io/github/stars/formatools/forma?style=social"/>
</p>    

**Forma** - Kotlin first, Meta Build System with Android and Gradle support. Opinionated, scalable, thoughtfully structured, type-safe and guided way to declare your project structure. Distributed as a Gradle plugin, Forma helps developers to shift focus from `Build Configuration` to `Project Structure Declaration`, abstracting away build configuration complexity.

- You don't need to be a gradle expert anymore
- Get rid of project configuration bad practices
- Type-safe, single method configuration for your targets, no room for error
- Built-in dependency visibility rules
- Target types - enforce scalable project structure
- High-performance builds: Gradle best practices are applied automatically
- Dependencies framework - helps developers to understand and deal with transitive dependencies hell
- Extensible - be the expert when you need to!
- And much more...

⚠️ We are using `target` term to express application components(e.g. modules or projects, depending in the context) across documentation and code, there is couple of reasons for that. `Module` term often confused with Dagger modules which makes communication harder, `project` from the other hand used only in Gradle context but not in other build systems like Buck and Bazel.

⚠️ This is early *alpha* release - please do try this at home🏠

Easiest way to start is here >> ‼️ https://github.com/stepango/FormaShowcase ‼️

[Presentation Link](https://www.beautiful.ai/player/-MLn7RnBBWeh7vePDoDq)

Configuration made easy (Gradle 6.8+):
```gradle
// settings.gradle.kts
dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        jcenter()
    }
}

```


``` gradle
// root build.gradle.kts
buildscript {
    repositories {
        google()
    }
}

plugins {
    id("tools.forma.android") version "0.0.1"
}

// Configure shared aspects of your android Project
androidProjectConfiguration(
    minSdk = 21,
    targetSdk = 29,
    compileSdk = 29,
    kotlinVersion = "1.4.10",
    agpVersion = "4.0.0",
    versionCode = 1,
    versionName = "1.0"
)
```

Your kotlin android library

``` gradle
// Single method, type-safe creation of your target
// Plugins applied automatically
// Project configuration shared between targets
androidLibrary(
    // Mandatory, visible from build configuration
    packageName = "com.stepango.example",
    // External dependencies declaration, one universal syntax
    dependencies = deps(
        google.material,
        androidx.appcompat,
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
        test.espresso
    )
)
```
## Progress

|    Supported target types     | implemented | purpose                  | validation |
|:-----------------------------:|:-----------:|:------------------------:|:-----------:
|         androidBinary         |      ✅      | Genearte single APK     |      ✅    |
|         androidApp            |      ✅      | Application class       |    partial |
|         androidLibrary        |      ✅      | Android library         |    partial |
|           dataBinding         |      ✅      | Data Binding Layouts    |    partial |
|       dataBindingAdapters     |      ✅      | Data Binding Adapters   |    partial |
|         androidWidget         |      ✅      | Custom View             |    partial |
|           androidRes          |      ✅      | Resources Only          |      ✅    |
|        androidTestUtils       |      ✅      | Shared code for Android tests |✅    |
|          androidUtils         |      ✅      | Library extensions      |    partial |
|           testUtils           |      ✅      | Shared code for unit tests |   ✅    |
|             utils             |      ✅      | JVM Library extensions  |    partial |
|             library           |      ✅      | JVM Library             |    partial |
|             api               |      ✅      | Feature external API's  |    partial |
|             impl              |      ✅      | Feature implementation  |    partial |


Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a>
