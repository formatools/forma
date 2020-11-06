import com.stepango.forma.feature.AndroidLibraryFeatureConfiguration
import com.stepango.forma.feature.androidLibraryFeatureDefinition
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinAndroidFeatureDefinition
import com.stepango.forma.target.AndroidUtilTarget
import com.stepango.forma.target.UtilTarget
import com.stepango.forma.target.WidgetTarget
import com.stepango.forma.owner.NoOwner
import com.stepango.forma.owner.Owner
import com.stepango.forma.dependencies.applyDependencies
import com.stepango.forma.validation.validate
import com.stepango.forma.validation.validator
import com.stepango.forma.visibility.Public
import com.stepango.forma.visibility.Visibility
import org.gradle.api.Project

fun Project.widget(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {
    validate(WidgetTarget)

    val featureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName = packageName,
        testInstrumentationRunnerClass = testInstrumentationRunner,
        consumerMinificationFiles = consumerMinificationFiles,
        manifestPlaceholders = manifestPlaceholders
    )

    applyFeatures(
        androidLibraryFeatureDefinition(featureConfiguration),
        kotlinAndroidFeatureDefinition()
    )

    applyDependencies(
        validator = validator(WidgetTarget, UtilTarget, AndroidUtilTarget),
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies
    )
}
