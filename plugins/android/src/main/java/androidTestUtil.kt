import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.AndroidTargetRegistry
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.deps.core.applyDependencies
import tools.forma.deps.core.FormaDependency
import tools.forma.validation.asValidator
import tools.forma.validation.validate

fun Project.androidTestUtil(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency()
) {
    AndroidTargetRegistry.selfValidator(AndroidTargetTypes.androidTestUtil).asValidator().validate(target)

    val androidFeatureConfig = AndroidLibraryFeatureConfiguration(
        packageName
    )
    applyFeatures(
        androidLibraryFeatureDefinition(androidFeatureConfig),
        kotlinAndroidFeatureDefinition()
    )

    applyDependencies(
        validator = AndroidTargetRegistry.validatorFor(AndroidTargetTypes.androidTestUtil).asValidator(),
        dependencies = dependencies
    )
}
