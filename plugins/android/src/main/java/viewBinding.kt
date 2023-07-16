import org.gradle.api.Project
import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.owner.NoOwner
import tools.forma.owner.Owner
import tools.forma.android.target.AndroidUtilTargetTemplate
import tools.forma.android.target.ApiTargetTemplate
import tools.forma.android.target.LibraryTargetTemplate
import tools.forma.android.target.ResourcesTargetTemplate
import tools.forma.android.target.ViewBindingTargetTemplate
import tools.forma.android.target.WidgetTargetTemplate
import tools.forma.android.validation.onlyAllowLayouts
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.deps_core.FormaDependency
import tools.forma.deps_core.applyDependencies
import tools.forma.validation.validate
import tools.forma.validation.validator

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

    target.validate(ViewBindingTargetTemplate)
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
        validator = validator(
            ApiTargetTemplate,
            WidgetTargetTemplate,
            ResourcesTargetTemplate,
            LibraryTargetTemplate,
            AndroidUtilTargetTemplate,
        ),
        dependencies = dependencies,
        repositoriesConfiguration = Forma.settings.repositories
    )
}
