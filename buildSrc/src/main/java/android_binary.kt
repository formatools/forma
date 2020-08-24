import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

// TODO: maybe enum for dagger, kotlin
// TODO: maybe remove, let devs use default config capabilities for root
fun Project.android_binary(
    packageName: String,
    dependencies: Dependency = EmptyDependency,
    testDependencies: Dependency = EmptyDependency,
    androidTestDependencies: Dependency = EmptyDependency,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any>  = emptyMap(),
    testInstrumentationRunner: String = androidJunitRunner
) = android_binary(
    packageName,
    dependencies,
    testDependencies,
    androidTestDependencies,
    buildConfiguration,
    testInstrumentationRunner,
    consumerMinificationFiles,
    manifestPlaceholders,
    Grip.configuration
)

private fun Project.android_binary(
    packageName: String,
    dependencies: Dependency,
    testDependencies: Dependency,
    androidTestDependencies: Dependency,
    buildConfiguration: BuildConfiguration,
    testInstrumentationRunner: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any> = emptyMap(),
    configuration: Configuration = Grip.configuration
) {
    apply(plugin = "com.android.application")
    applyConfiguration(configuration, packageName, buildConfiguration, testInstrumentationRunner, consumerMinificationFiles, manifestPlaceholders)
    applyDependencies(dependencies, testDependencies, androidTestDependencies)
}