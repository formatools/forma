import com.stepango.forma.LibraryModule
import com.stepango.forma.Validator
import com.stepango.forma.feature.AndroidLibraryFeatureConfiguration
import com.stepango.forma.feature.androidLibraryFeatureDefinition
import com.stepango.forma.validator
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

fun Project.androidLibrary(
    packageName: String,
    dependencies: FormaDependency = emptyDependency(),
    projectDependencies: ProjectDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    testInstrumentationRunner: String = androidJunitRunner,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap(),
    validator: Validator = validator(LibraryModule),
    dataBinding: Boolean = false
) {
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
        dataBinding = dataBinding
    )
    applyFeatures(
        androidLibraryFeatureDefinition(libraryFeatureConfiguration)
    )
    apply(plugin = "kotlin-android")
    // TODO Replace this flow for call KotlinAndroid, KotlinKapt definitions into applyFeatures {...}
    if (dataBinding) {
        apply(plugin = "kotlin-kapt")
    }
    applyDependencies(
        validator = libraryFeatureConfiguration.dependencyValidator,
        dependencies = dependencies,
        projectDependencies = projectDependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies
    )
    // TODO Fix logic validation
//    validator.validate(this)
}
