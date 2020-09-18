import com.stepango.forma.FormaConfiguration
import com.stepango.forma.Library
import com.stepango.forma.Validator
import com.stepango.forma.validator
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

@Suppress("UnstableApiUsage")
internal fun Project.android_library(
    dependencies: NamedDependency,
    projectDependencies: ProjectDependency,
    testDependencies: FormaDependency,
    testUtilDependencies: ProjectDependency,
    buildConfiguration: BuildConfiguration,
    testInstrumentationRunner: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any>,
    androidTestDependencies: NamedDependency,
    formaConfiguration: FormaConfiguration,
    validator: Validator = validator(Library)
) {
    apply(plugin = "com.android.library")
    applyLibraryConfiguration(formaConfiguration, buildConfiguration, testInstrumentationRunner, consumerMinificationFiles, manifestPlaceholders)
    applyDependencies(
        formaConfiguration = formaConfiguration,
        dependencies = dependencies,
        projectDependencies = projectDependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies
    )
    validator.validate(this)
}

fun Project.android_library(
    packageName: String, // TODO: manifest placeholder for package
    dependencies: NamedDependency = emptyDependency(),
    projectDependencies: ProjectDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    testUtilDependencies: ProjectDependency = emptyDependency(),
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {
    android_library(
        dependencies,
        projectDependencies,
        testDependencies,
        testUtilDependencies,
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
