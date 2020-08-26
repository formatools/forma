import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

fun Project.android_library(
    dependencies: Dependency = EmptyDependency,
    testDependencies: Dependency = EmptyDependency,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    androidTestDependencies: Dependency,
    manifestPlaceholders: Map<String, Any> = emptyMap()
) = android_library(
    dependencies,
    testDependencies,
    buildConfiguration,
    testInstrumentationRunner,
    consumerMinificationFiles,
    manifestPlaceholders,
    androidTestDependencies,
    Forma.configuration
)

@Suppress("UnstableApiUsage")
internal fun Project.android_library(
    dependencies: Dependency,
    testDependencies: Dependency,
    buildConfiguration: BuildConfiguration,
    testInstrumentationRunner: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any>,
    androidTestDependencies: Dependency,
    configuration: Configuration
) {
    apply(plugin = "com.android.library")
    applyLibraryConfiguration(configuration, buildConfiguration, testInstrumentationRunner, consumerMinificationFiles, manifestPlaceholders)
    applyDependencies(dependencies, testDependencies, androidTestDependencies)
}