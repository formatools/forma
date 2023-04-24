plugins {
    id("com.gradle.plugin-publish") version "1.1.0"
    `kotlin-dsl`
    id("org.jetbrains.kotlin.jvm") version embeddedKotlinVersion
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest(embeddedKotlinVersion)
        }

        // Create a new test suite
        val functionalTest by registering(JvmTestSuite::class) {
            // Use Kotlin Test test framework
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
        create("Depgen") {
            id = "tools.forma.depgen"
            implementationClass = "tools.forma.depgen.DepgenPlugin"
        }
    }
}

gradlePlugin.testSourceSets(sourceSets["functionalTest"])

tasks.named<Task>("check") {
    // Include functionalTest as part of the check lifecycle
    dependsOn(testing.suites.named("functionalTest"))
}
