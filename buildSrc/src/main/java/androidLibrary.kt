import com.stepango.forma.feature.*
import com.stepango.forma.module.LibraryModule
import com.stepango.forma.utils.BuildConfiguration
import com.stepango.forma.utils.applyDependencies
import com.stepango.forma.validation.Validator
import com.stepango.forma.validation.validator
import org.gradle.api.Project

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
