import com.stepango.forma.feature.AndroidLibraryFeatureConfiguration
import com.stepango.forma.feature.androidLibraryFeatureDefinition
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinAndroidFeatureDefinition
import com.stepango.forma.feature.kotlinKaptFeatureDefinition
import com.stepango.forma.module.LibraryModule
import com.stepango.forma.utils.BuildConfiguration
import com.stepango.forma.utils.applyDependencies
import com.stepango.forma.validation.EmptyValidator
import com.stepango.forma.validation.validate
import org.gradle.api.Project

fun Project.androidLibrary(
    packageName: String,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    testInstrumentationRunner: String = androidJunitRunner,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {
    validate(LibraryModule)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders
    )
    applyFeatures(
        androidLibraryFeatureDefinition(libraryFeatureConfiguration),
        kotlinAndroidFeatureDefinition()
    )

    if (dependencies.hasConfigType(Kapt)) {
        applyFeatures(
            kotlinKaptFeatureDefinition()
        )
    }

    applyDependencies(
        validator = EmptyValidator,
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies
    )
}

