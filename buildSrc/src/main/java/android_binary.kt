import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

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
    Grip.configuration
)

private fun Project.android_binary(
    packageName: String,
    projectDependencies: Dependency,
    buildConfiguration: BuildConfiguration,
    testInstrumentationRunner: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any> = emptyMap(),
    configuration: Configuration = Grip.configuration
) {
    apply(plugin = "com.android.application")
    applyConfiguration(configuration, packageName, buildConfiguration, testInstrumentationRunner, consumerMinificationFiles, manifestPlaceholders, isApp = true)
    applyDependencies(projectDependencies = projectDependencies)
}