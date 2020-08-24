import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

fun Project.kt_android_library(
    packageName: String,
    dependencies: Dependency = EmptyDependency,
    testDependencies: Dependency = EmptyDependency,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    androidTestDependencies: Dependency = EmptyDependency,
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {
    android_library(
        packageName,
        dependencies,
        testDependencies,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
        androidTestDependencies,
        Grip.configuration
    )
    apply(plugin = "kotlin-android")
}

fun Project.android_library(
    packageName: String,
    dependencies: Dependency = EmptyDependency,
    testDependencies: Dependency = EmptyDependency,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    androidTestDependencies: Dependency,
    manifestPlaceholders: Map<String, Any> = emptyMap()
) = android_library(
    packageName,
    dependencies,
    testDependencies,
    buildConfiguration,
    testInstrumentationRunner,
    consumerMinificationFiles,
    manifestPlaceholders,
    androidTestDependencies,
    Grip.configuration
)

@Suppress("UnstableApiUsage")
internal fun Project.android_library(
    packageName: String,
    dependencies: Dependency,
    testDependencies: Dependency,
    buildConfiguration: BuildConfiguration,
    testInstrumentationRunner: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any>,
    androidTestDependencies: Dependency,
    configuration: Configuration
) {
    apply(plugin = "com.android.application")
    applyConfiguration(configuration, packageName, buildConfiguration, testInstrumentationRunner, consumerMinificationFiles, manifestPlaceholders)
    applyDependencies(dependencies, testDependencies, androidTestDependencies)
}