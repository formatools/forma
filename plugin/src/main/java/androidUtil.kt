import com.stepango.forma.feature.AndroidLibraryFeatureConfiguration
import com.stepango.forma.feature.androidLibraryFeatureDefinition
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinAndroidFeatureDefinition
import com.stepango.forma.module.AndroidUtilModule
import com.stepango.forma.module.TestUtilModule
import com.stepango.forma.owner.NoOwner
import com.stepango.forma.owner.Owner
import com.stepango.forma.dependencies.applyDependencies
import com.stepango.forma.validation.validate
import com.stepango.forma.validation.validator
import com.stepango.forma.visibility.Public
import com.stepango.forma.visibility.Visibility
import org.gradle.api.Project


/**
 * TODO
 *
 * External AAR Libraries extensions - usage:
 * Add libraries to dependencies e.g. appcompat
 * retrofit dependency cluster should include extension
 *
 * ```
 * val retrofit = transitiveDeps(
 *     "com.google.design:material:2.8.0"
 * ) + deps(
 *     project("material-android-util")
 * )
 * ```
 *
 */
fun Project.androidUtil(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
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

    applyDependencies(
        validator = validator(AndroidUtilModule, TestUtilModule),
        dependencies = dependencies,
        testDependencies = testDependencies
    )
}