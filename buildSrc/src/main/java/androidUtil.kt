import com.stepango.forma.feature.AndroidLibraryFeatureConfiguration
import com.stepango.forma.feature.androidLibraryFeatureDefinition
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinAndroidFeatureDefinition
import com.stepango.forma.module.AndroidUtilModule
import com.stepango.forma.module.TestUtilModule
import com.stepango.forma.utils.applyDependencies
import com.stepango.forma.validation.validate
import com.stepango.forma.validation.validator
import org.gradle.api.Project

fun Project.androidUtil(
    packageName: String,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: FormaDependency = emptyDependency()
) {
    validate(AndroidUtilModule)

    val androidFeatureConfig = AndroidLibraryFeatureConfiguration(
        packageName
    )

    applyFeatures(
        androidLibraryFeatureDefinition(androidFeatureConfig),
        kotlinAndroidFeatureDefinition()
    )

    applyDependencies(
        validator = validator(AndroidUtilModule, TestUtilModule),
        dependencies = dependencies,
        testDependencies = testDependencies
    )
}