import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

fun Project.kt_android_library(
    packageName: String, // TODO: manifest placeholder for package
    dependencies: NamedDependency = emptyDependency(),
    projectDependencies: ProjectDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    androidTestDependencies: NamedDependency = emptyDependency(),
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
    dependencies {
        kotlin.stdlib_jdk8.names.forEach {
            implementation(it.name)
        }
    }
}