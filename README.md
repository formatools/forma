# forma <img src="./img/rings.svg" width="30" height="30">
![Android CI](https://github.com/stepango/forma/workflows/Android%20CI/badge.svg)
[![Gradle Plugin](https://img.shields.io/maven-metadata/v/https/plugins.gradle.org/m2/com/stepango/forma/com.stepango.forma.gradle.plugin/maven-metadata.xml.svg?colorB=007ec6&label=Gradle%20Plugin)](https://plugins.gradle.org/plugin/com.stepango.forma)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/stepango/forma)
![GitHub](https://img.shields.io/github/license/stepango/forma)
![GitHub contributors](https://img.shields.io/github/contributors/stepango/forma)
![GitHub top language](https://img.shields.io/github/languages/top/stepango/forma)
![GitHub closed pull requests](https://img.shields.io/github/issues-pr-closed/stepango/forma)

![GitHub Repo stars](https://img.shields.io/github/stars/stepango/forma?style=social)

<img src="./img/press.svg" width="128" height="128">

⚠️ This is early *alpha* release - please do try this at home🏠

Forma - Meta Build System with Android and Gradle support. Opinionated, scalable, thoughtfully structured, type-safe and guided way to declare your project structure. Distributed as a Gradle plugin, Forma helps developers to shift focus from `Build Configuration` to `Project Structure Declaration`, abstracting away build configuration complexity.

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

Easiest way to start is here >> ‼️ https://github.com/stepango/FormaShowcase ‼️

Configuration made easy:

``` gradle
// root build.gradle.kts
buildscript {
    repositories {
        google()
    }
}

plugins {
    id("com.stepango.forma") version "0.0.3"
}

// Configure shared aspects of your android Project
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
