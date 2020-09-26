import com.stepango.forma.LibraryModule
import com.stepango.forma.Validator
import com.stepango.forma.feature.AndroidLibraryFeatureConfiguration
import com.stepango.forma.feature.androidLibraryFeatureDefinition
import com.stepango.forma.feature.kotlinAndroidFeatureDefinition
import com.stepango.forma.feature.kotlinKaptFeatureDefinition
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
    dataBinding: Boolean = false
) {
    val validator: Validator = validator(LibraryModule)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
        dataBinding = dataBinding
    )
    applyFeatures(
        androidLibraryFeatureDefinition(libraryFeatureConfiguration),
        kotlinAndroidFeatureDefinition(),
        if (dataBinding) kotlinKaptFeatureDefinition() else emptyFeatureDefinition
    )

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
