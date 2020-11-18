import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.feature.kotlinKaptFeatureDefinition
import tools.forma.android.target.AndroidUtilTarget
import tools.forma.android.target.ApiTarget
import tools.forma.android.target.ResourcesTarget
import tools.forma.android.target.LibraryTarget
import tools.forma.android.target.DataBindingAdapterTarget
import tools.forma.android.target.DataBindingTarget
import tools.forma.android.target.WidgetTarget
import tools.forma.android.validation.validate
import tools.forma.android.validation.validator
import tools.forma.android.owner.Owner
import tools.forma.android.owner.NoOwner
import tools.forma.android.validation.disallowResources
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.android.dependencies.applyDependencies

/**
 * Android Data Binding Target - Data Binding layouts collection
 *
 * Only MVVM interfaces and layouts allowed, no business logic or other resource types
 *
 * @param packageName - Application package name, used for publishing
 * @param owner - owner of the target, team responsible for maintenance
 * @param visibility - visibility scope for the target
 * @param dependencies - list of external and project dependencies for the target
 * @param consumerMinificationFiles - Proguard/R8 minification files list
 */
fun Project.dataBinding(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    consumerMinificationFiles: Set<String> = emptySet()
) {
    checkDataBindingFlag()
    validate(DataBindingTarget)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName = packageName,
        consumerMinificationFiles = consumerMinificationFiles,
        dataBinding = true
    )
    applyFeatures(
        androidLibraryFeatureDefinition(libraryFeatureConfiguration),
        kotlinAndroidFeatureDefinition(),
        kotlinKaptFeatureDefinition()
    )
    applyDependencies(
        validator = validator(
            ApiTarget,
            WidgetTarget,
            AndroidUtilTarget,
            DataBindingAdapterTarget,
            ResourcesTarget,
            LibraryTarget
        ),
        dependencies = dependencies
    )
}

/**
 * Android Data Binding Adapters - Shared Data Binding Adapters collection
 *
 * Only DataBinding adapters allowed, no resources or business logic
 *
 * @param packageName - Application package name, used for publishing
 * @param owner - owner of the target, team responsible for maintenance
 * @param visibility - visibility scope for the target
 * @param dependencies - list of external and project dependencies for the target
 * @param consumerMinificationFiles - Proguard/R8 minification files list
 */
fun Project.dataBindingAdapters(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    consumerMinificationFiles: Set<String> = emptySet()
) {
    checkDataBindingFlag()

    disallowResources()

    validate(DataBindingAdapterTarget)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName = packageName,
        consumerMinificationFiles = consumerMinificationFiles,
        dataBinding = true
    )
    applyFeatures(
        androidLibraryFeatureDefinition(libraryFeatureConfiguration),
        kotlinAndroidFeatureDefinition(),
        kotlinKaptFeatureDefinition()
    )
    applyDependencies(
        validator = validator(
            WidgetTarget,
            AndroidUtilTarget,
            ResourcesTarget
        ),
        dependencies = dependencies
    )
}

private fun checkDataBindingFlag() {
    if (!Forma.configuration.dataBinding) {
        throw IllegalArgumentException("Please enable dataBinding feature trough androidProjectConfiguration")
    }
}