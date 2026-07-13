import org.gradle.api.Project
import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.AndroidTargetRegistry
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.android.validation.onlyAllowLayouts
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.applyDependencies
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.validation.asValidator
import tools.forma.validation.validate

/**
 * Android View Binding Target - View Binding layouts collection
 *
 * Only layouts allowed, no other resource types
 *
 * @param packageName - Application package name, used for publishing
 * @param owner - owner of the target, team responsible for maintenance
 * @param visibility - visibility scope for the target
 * @param dependencies - list of external and project dependencies for the target
 * @param consumerMinificationFiles - Proguard/R8 minification files list
 */
fun Project.viewBinding(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    consumerMinificationFiles: Set<String> = emptySet()
) {
    onlyAllowLayouts()

    AndroidTargetRegistry.selfValidator(AndroidTargetTypes.viewBinding).asValidator().validate(target)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName = packageName,
        consumerMinificationFiles = consumerMinificationFiles,
        viewBinding = true
    )
    applyFeatures(
        androidLibraryFeatureDefinition(libraryFeatureConfiguration),
        kotlinAndroidFeatureDefinition(),
    )
    applyDependencies(
        validator = AndroidTargetRegistry.validatorFor(AndroidTargetTypes.viewBinding).asValidator(),
        dependencies = dependencies
    )
}
