import com.stepango.forma.feature.AndroidLibraryFeatureConfiguration
import com.stepango.forma.feature.androidLibraryFeatureDefinition
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinAndroidFeatureDefinition
import com.stepango.forma.feature.kotlinKaptFeatureDefinition
import com.stepango.forma.module.AndroidUtilModule
import com.stepango.forma.module.TestUtilModule
import com.stepango.forma.owner.NoOwner
import com.stepango.forma.owner.Owner
import com.stepango.forma.utils.applyDependencies
import com.stepango.forma.validation.validate
import com.stepango.forma.validation.validator
import org.gradle.api.Project

fun Project.androidUtil(
    packageName: String,
    owner: Owner = NoOwner,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: FormaDependency = emptyDependency()
) {

    //TODO unify with util, use androidJar dependency
    validate(AndroidUtilModule)

    val androidFeatureConfig = AndroidLibraryFeatureConfiguration(
        packageName
    )

    applyFeatures(
        androidLibraryFeatureDefinition(androidFeatureConfig),
        kotlinAndroidFeatureDefinition()
    )

    if (dependencies.hasConfigType(Kapt)) {
        applyFeatures(
            kotlinKaptFeatureDefinition()
        )
    }

    applyDependencies(
        validator = validator(AndroidUtilModule, TestUtilModule),
        dependencies = dependencies,
        testDependencies = testDependencies
    )
}