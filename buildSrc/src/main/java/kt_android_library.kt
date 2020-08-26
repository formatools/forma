import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

fun Project.kt_android_library(
    packageName: String, // TODO: manifest placeholder for package
    dependencies: Dependency = EmptyDependency,
    testDependencies: Dependency = EmptyDependency,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    androidTestDependencies: Dependency = EmptyDependency,
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {
    android_library(
        dependencies,
        testDependencies,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
        androidTestDependencies,
        Forma.configuration
    )
    apply(plugin = "kotlin-android")
}