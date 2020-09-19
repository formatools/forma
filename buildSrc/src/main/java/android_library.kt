import com.android.build.gradle.LibraryExtension
import com.stepango.forma.*
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

@Suppress("UnstableApiUsage")
@Deprecated(message = "Use Project.androidLibrary(...)", level = DeprecationLevel.WARNING)
internal fun Project.android_library(
    dependencies: NamedDependency,
    projectDependencies: ProjectDependency,
    testDependencies: FormaDependency,
    testUtilDependencies: ProjectDependency,
    kaptDependencies: FormaDependency,
    buildConfiguration: BuildConfiguration,
    testInstrumentationRunner: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any>,
    androidTestDependencies: NamedDependency,
    formaConfiguration: FormaConfiguration,
    validator: Validator = validator(Library)
) {
    apply(plugin = "com.android.library")
    apply(plugin = "kotlin-android")
    if (kaptDependencies != EmptyDependency) {
        apply(plugin = "kotlin-kapt")
    }
    applyLibraryConfiguration(formaConfiguration, buildConfiguration, testInstrumentationRunner, consumerMinificationFiles, manifestPlaceholders)
    applyDependencies(
        validator = EmptyValidator, //TODO proper validator here
        formaConfiguration = formaConfiguration,
        dependencies = dependencies,
        projectDependencies = projectDependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies,
        kaptDependencies = kaptDependencies
    )
    validator.validate(this)
}
@Deprecated(message ="Use Project.androidLibrary(...)", level = DeprecationLevel.WARNING)
fun Project.android_library(
    packageName: String, // TODO: manifest placeholder for package
    dependencies: NamedDependency = emptyDependency(),
    projectDependencies: ProjectDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    testUtilDependencies: ProjectDependency = emptyDependency(),
    kaptDependencies: FormaDependency = emptyDependency(),
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
        kaptDependencies,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
        androidTestDependencies,
        Forma.configuration
    )
    dependencies {
        kotlin.stdlib_jdk8.names.forEach {
            implementation(it.name)
        }
    }
}

fun Project.androidLibrary(
    packageName: String,
    dependencies: FormaDependency = emptyDependency(),
    projectDependencies: ProjectDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    kaptDependencies: FormaDependency = emptyDependency(),
    testInstrumentationRunner: String = androidJunitRunner,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap(),
    validator: Validator = validator(Library)
) {
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders
    )

    applyFeatures(
        androidLibraryFeatureDefinition(libraryFeatureConfiguration)
    )

    applyDependencies(
        validator = libraryFeatureConfiguration.dependencyValidator,
        dependencies = dependencies,
        projectDependencies = projectDependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies,
        kaptDependencies = kaptDependencies
    )

    validator.validate(this)
}

private data class AndroidLibraryFeatureConfiguration(
    val packageName: String,
    val buildConfiguration: BuildConfiguration,
    val testInstrumentationRunnerClass: String,
    val consumerMinificationFiles: Set<String>,
    val manifestPlaceholders: Map<String, Any> = emptyMap(),
    val dependencyValidator: Validator = EmptyValidator,
    val selfValidator: Validator = validator(Library)
)

private fun androidLibraryFeatureDefinition(
    featureConfiguration: AndroidLibraryFeatureConfiguration
) = FeatureDefinition(
    pluginName = "com.android.library",
    pluginExtension = LibraryExtension::class,
    featureConfiguration = featureConfiguration,
    configuration = { extension, feature, _, formaConfiguration ->
        with(extension) {
            compileSdkVersion(formaConfiguration.compileSdk)

            defaultConfig.applyFrom(
                formaConfiguration,
                feature.testInstrumentationRunnerClass,
                feature.consumerMinificationFiles,
                feature.manifestPlaceholders
            )

            buildTypes.applyFrom(feature.buildConfiguration)
            compileOptions.applyFrom(formaConfiguration)

            // TODO may be don't need
            // buildFeatures.dataBinding = formaConfiguration.databindings
        }
    }
)
