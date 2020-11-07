import com.stepango.forma.feature.AndroidLibraryFeatureConfiguration
import com.stepango.forma.feature.androidLibraryFeatureDefinition
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinAndroidFeatureDefinition
import com.stepango.forma.target.ApplicationTarget
import com.stepango.forma.owner.NoOwner
import com.stepango.forma.owner.Owner
import com.stepango.forma.utils.BuildConfiguration
import com.stepango.forma.dependencies.applyDependencies
import com.stepango.forma.validation.EmptyValidator
import com.stepango.forma.validation.validate
import com.stepango.forma.visibility.Public
import com.stepango.forma.visibility.Visibility
import org.gradle.api.Project

/**
 * TODO Can't depend on widgets, cant depend on databindings
 */
fun Project.androidApp(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    testInstrumentationRunner: String = androidJunitRunner,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {
    validate(ApplicationTarget)
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

    applyDependencies(
        validator = EmptyValidator,
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies
    )
}
