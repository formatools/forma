import com.stepango.forma.feature.*
import com.stepango.forma.utils.BuildConfiguration
import com.stepango.forma.dependencies.applyDependencies
import com.stepango.forma.target.*
import com.stepango.forma.validation.validate
import com.stepango.forma.validation.validator
import org.gradle.api.Project

fun Project.impl(
    packageName: String,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    testInstrumentationRunner: String = androidJunitRunner,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap(),
    dataBinding: Boolean = false
) {
    validate(ImplTarget)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
        selfValidator = validator(ImplTarget),
        dataBinding = dataBinding
    )
    applyFeatures(
        androidLibraryFeatureDefinition(libraryFeatureConfiguration),
        kotlinAndroidFeatureDefinition()
    )

    applyDependencies(
        validator = validator(
            ApiTarget,
            AndroidUtilTarget,
            TestUtilTarget,
            UtilTarget,
            LibraryTarget,
            DataBindingAdapterTarget,
            DataBindingTarget
        ),
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies
    )
}

