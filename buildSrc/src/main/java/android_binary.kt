import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

/**
 * Application entry point. Manifest + minimal set of resources + root android project com.stepango.forma.internal.getDependency only.
 * No library dependencies, no source code.
 */
fun Project.android_binary(
    packageName: String,
    projectDependencies: Dependency = EmptyDependency,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any>  = emptyMap(),
    testInstrumentationRunner: String = androidJunitRunner
) = android_binary(
    packageName,
    projectDependencies,
    buildConfiguration,
    testInstrumentationRunner,
    consumerMinificationFiles,
    manifestPlaceholders,
    Forma.configuration
)

private fun Project.android_binary(
    packageName: String,
    projectDependencies: Dependency,
    buildConfiguration: BuildConfiguration,
    testInstrumentationRunner: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any> = emptyMap(),
    configuration: Configuration = Forma.configuration
) {
    apply(plugin = "com.android.application")
    applyAppConfiguration(configuration, packageName, buildConfiguration, testInstrumentationRunner, consumerMinificationFiles, manifestPlaceholders)
    applyDependencies(projectDependencies = projectDependencies)
}
