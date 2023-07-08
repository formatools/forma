@file:Suppress("UnstableApiUsage")

plugins {
    id("com.gradle.plugin-publish") version "1.2.0"
    id("org.jetbrains.kotlin.jvm") version embeddedKotlinVersion
}

version = "0.2.0"
group = "tools.forma"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
}

val javaLanguageVersion = JavaLanguageVersion.of(11)
kotlin {
    jvmToolchain {
        languageVersion.set(javaLanguageVersion)
    }
}

java {
    toolchain {
        languageVersion.set(javaLanguageVersion)
    }
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test framework
            useKotlinTest(embeddedKotlinVersion)
        }

        // Create a new test suite
        val functionalTest by registering(JvmTestSuite::class) {
            // Use Kotlin Test framework
            useKotlinTest(embeddedKotlinVersion)

            dependencies {
                // functionalTest test suite depends on the production code in tests
                implementation(project())
            }

            targets {
                all {
                    // This test suite should run after the built-in test suite has run its tests
                    testTask.configure { shouldRunAfter(test) }
                }
            }
        }
    }
}

gradlePlugin {
    website.set("https://forma.tools/")
    vcsUrl.set("https://github.com/formatools/forma.git")
    plugins {
        create("Includer") {
            id = "tools.forma.includer"
            displayName = "Includer - automatically includes all gradle projects"
            description = "Save yourself from writing and supporting a lot of boilerplate code"
            implementationClass = "tools.forma.includer.IncluderPlugin"
            tags.set(
                listOf(
                    "kotlin",
                    "android",
                    "structure",
                    "dependencies",
                    "module",
                    "rules",
                    "project"
                )
            )
        }
    }
}

gradlePlugin.testSourceSets(sourceSets["functionalTest"])

tasks.named<Task>("check") {
    // Include functionalTest as part of the check lifecycle
    dependsOn(testing.suites.named("functionalTest"))
}
