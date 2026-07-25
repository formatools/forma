import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.math.BigDecimal
import java.util.concurrent.Callable

/**
 * Modules that run unit tests and contribute to Jacoco reports.
 * Platform facades without unit suites (android/target/…) are sample-build covered.
 */
internal val coverageReportModules = setOf("core", "config", "deps", "jvm")

/**
 * Minimum LINE covered ratio for the **happy-path** class set (see
 * [happyPathClassIncludes]). Instruction counters are noisier on Kotlin.
 */
internal val minHappyPathLineCoverage: BigDecimal = BigDecimal("0.60")

/**
 * Jacoco class include patterns for happy-path surface:
 * - forma-core engine (all packages)
 * - config data model (not plugin entry)
 * - deps catalog name generators + type-owned plugin registry
 * - jvm target registry (not AGP/DSL apply paths)
 *
 * Gradle Project apply paths (`applyDependencies`, fleet tasks, plugin mains,
 * VersionCatalogBuilder Settings API) stay out of the gate — they need TestKit.
 */
internal val happyPathClassIncludes =
    listOf(
        "**/tools/forma/core/**",
        "**/tools/forma/config/AndroidProjectSettings*",
        "**/tools/forma/config/FormaBuildFeatures*",
        "**/tools/forma/config/FormaFeatureFlags*",
        "**/tools/forma/config/FormaSettingsStore*",
        "**/tools/forma/config/DependencyValidation*",
        "**/tools/forma/deps/catalog/Generators*",
        "**/tools/forma/deps/core/TargetPlugin*",
        "**/tools/forma/deps/core/DefaultTargetPlugin*",
        "**/tools/forma/deps/core/ConfigurationType*",
        "**/tools/forma/deps/core/CustomConfiguration*",
        "**/tools/forma/deps/core/Implementation*",
        "**/tools/forma/deps/core/CompileOnly*",
        "**/tools/forma/deps/core/RuntimeOnly*",
        "**/tools/forma/deps/core/AnnotationProcessor*",
        "**/tools/forma/deps/core/Ksp*",
        "**/tools/forma/deps/core/DepSpec*",
        "**/tools/forma/deps/core/TargetSpec*",
        "**/tools/forma/deps/core/FileSpec*",
        "**/tools/forma/deps/core/NameSpec*",
        "**/tools/forma/deps/core/PlatformSpec*",
        "**/tools/forma/deps/core/FormaDependency*",
        "**/tools/forma/deps/core/EmptyDependency*",
        "**/tools/forma/deps/core/NamedDependency*",
        "**/tools/forma/deps/core/PlatformDependency*",
        "**/tools/forma/deps/core/TargetDependency*",
        "**/tools/forma/deps/core/FileDependency*",
        "**/tools/forma/deps/core/MixedDependency*",
        // F-099 pure conditional-deps resolver (applyDependencies stays out of gate)
        "**/tools/forma/deps/core/ConditionalDependency*",
        "**/tools/forma/jvm/target/**",
        // F-106/F-107 KMP pure registry + settings + resolution (Project apply paths stay out)
        "**/tools/forma/kmp/target/**",
        "**/tools/forma/kmp/settings/**",
        "**/tools/forma/kmp/feature/KmpPluginIds*",
        "**/tools/forma/kmp/feature/KmpFeatureResolution*",
    )

/** Apply Jacoco reporting to a plugins subproject (when it has Java + tests). */
fun Project.configureFormaCoverage() {
    pluginManager.withPlugin("java") {
        apply<JacocoPlugin>()

        extensions.configure<JacocoPluginExtension> {
            toolVersion = "0.8.13"
        }

        tasks.withType<Test>().configureEach {
            extensions.configure<JacocoTaskExtension> {
                isIncludeNoLocationClasses = true
                excludes = listOf("jdk.internal.*")
            }
            finalizedBy(tasks.named("jacocoTestReport"))
        }

        tasks.named<JacocoReport>("jacocoTestReport") {
            dependsOn(tasks.withType<Test>())
            reports {
                xml.required.set(true)
                html.required.set(true)
                csv.required.set(false)
            }
        }
    }
}

/**
 * Root aggregate report + **happy-path coverage gate** (LINE ≥ 60%).
 *
 *   ./gradlew test jacocoRootReport jacocoHappyPathCoverageVerification
 *   open build/reports/jacoco/jacocoRootReport/html/index.html
 *   open build/reports/jacoco/jacocoHappyPathReport/html/index.html
 */
fun Project.registerFormaAggregateCoverageReport() {
    apply<JacocoPlugin>()

    fun coverageSubprojects(): List<Project> =
        subprojects.filter { it.name in coverageReportModules }

    fun mainClassDirs(): FileCollection =
        files(
            Callable {
                coverageSubprojects().flatMap { sp ->
                    listOf(
                        sp.layout.buildDirectory.dir("classes/kotlin/main").get().asFile,
                        sp.layout.buildDirectory.dir("classes/java/main").get().asFile,
                    ).filter { it.exists() }
                }
            }
        )

    fun happyPathClassDirs(): FileTree =
        mainClassDirs().asFileTree.matching {
            happyPathClassIncludes.forEach { include(it) }
        }

    fun mainSourceDirs(): FileCollection =
        files(
            Callable {
                coverageSubprojects().flatMap { sp ->
                    listOf(sp.file("src/main/java"), sp.file("src/main/kotlin")).filter { it.exists() }
                }
            }
        )

    fun execData(): FileCollection =
        files(
            Callable {
                coverageSubprojects().map {
                    it.layout.buildDirectory.file("jacoco/test.exec").get().asFile
                }.filter { it.exists() }
            }
        )

    fun testTaskPaths(): List<String> =
        coverageSubprojects().map { "${it.path}:test" }

    tasks.register<JacocoReport>("jacocoRootReport") {
        group = "verification"
        description = "Aggregate Jacoco report for unit-tested Forma plugin modules"

        dependsOn(Callable { testTaskPaths() })
        dependsOn(Callable { coverageSubprojects().map { "${it.path}:jacocoTestReport" } })

        classDirectories.from(mainClassDirs())
        sourceDirectories.from(mainSourceDirs())
        executionData.from(execData())

        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }

        doLast {
            logger.lifecycle("Aggregate Jacoco HTML: ${reports.html.outputLocation.get().asFile}")
        }
    }

    val happyPathReport =
        tasks.register<JacocoReport>("jacocoHappyPathReport") {
            group = "verification"
            description =
                "Jacoco report for Forma happy-path classes only (core engine + pure deps/config/jvm)"

            dependsOn(Callable { testTaskPaths() })

            classDirectories.from(happyPathClassDirs())
            sourceDirectories.from(mainSourceDirs())
            executionData.from(execData())

            reports {
                xml.required.set(true)
                html.required.set(true)
                csv.required.set(false)
            }

            doLast {
                logger.lifecycle(
                    "Happy-path Jacoco HTML: ${reports.html.outputLocation.get().asFile}"
                )
            }
        }

    tasks.register<JacocoCoverageVerification>("jacocoHappyPathCoverageVerification") {
        group = "verification"
        description =
            "Fail if happy-path LINE coverage is below " +
                "${minHappyPathLineCoverage.multiply(BigDecimal(100)).toPlainString()}%"

        dependsOn(happyPathReport)

        classDirectories.from(happyPathClassDirs())
        sourceDirectories.from(mainSourceDirs())
        executionData.from(execData())

        violationRules {
            rule {
                isEnabled = true
                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = minHappyPathLineCoverage
                }
            }
        }
    }

    // Wire into check so `./gradlew check` / CI `build` enforces the bar.
    tasks.named("check") {
        dependsOn("jacocoHappyPathCoverageVerification")
    }
}
