import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

fun Project.android_library(
    dependencies: NamedDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    androidTestDependencies: NamedDependency,
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
    dependencies: NamedDependency,
    testDependencies: FormaDependency,
    buildConfiguration: BuildConfiguration,
    testInstrumentationRunner: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any>,
    androidTestDependencies: NamedDependency,
    configuration: Configuration
) {
    apply(plugin = "com.android.library")
    applyLibraryConfiguration(configuration, buildConfiguration, testInstrumentationRunner, consumerMinificationFiles, manifestPlaceholders)
    applyDependencies(
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies
    )
}