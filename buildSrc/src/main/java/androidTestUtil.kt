import com.stepango.forma.feature.AndroidLibraryFeatureConfiguration
import com.stepango.forma.feature.androidLibraryFeatureDefinition
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinAndroidFeatureDefinition
import com.stepango.forma.module.AndroidTestUtilModule
import com.stepango.forma.module.TestUtilModule
import com.stepango.forma.utils.BuildConfiguration
import com.stepango.forma.utils.applyDependencies
import com.stepango.forma.validation.validator
import org.gradle.api.Project

data class AndroidFeatureConfig(
    val testInstrumentationRunnerClass: String = androidJunitRunner,
    val consumerMinificationFiles: Set<String> = emptySet(),
    val manifestPlaceholders: Map<String, Any> = emptyMap(),
    val buildConfiguration: BuildConfiguration = BuildConfiguration()
)

fun Project.androidTestUtil(
    packageName: String,
    dependencies: FormaDependency = emptyDependency()
) {
    // TODO refactor to single method call
    val nameValidator = validator(AndroidTestUtilModule)
    nameValidator.validate(this)

    val androidFeatureConfig = AndroidLibraryFeatureConfiguration(
        packageName
    )
    applyFeatures(
        androidLibraryFeatureDefinition(androidFeatureConfig),
        kotlinAndroidFeatureDefinition()
    )

    applyDependencies(
        validator = validator(AndroidTestUtilModule, TestUtilModule),
        dependencies = dependencies
    )
}
