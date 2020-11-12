import com.stepango.forma.feature.AndroidLibraryFeatureConfiguration
import com.stepango.forma.feature.androidLibraryFeatureDefinition
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinAndroidFeatureDefinition
import com.stepango.forma.feature.kotlinKaptFeatureDefinition
import com.stepango.forma.target.AndroidUtilTarget
import com.stepango.forma.target.ResourcesTarget
import com.stepango.forma.target.LibraryTarget
import com.stepango.forma.target.DataBindingAdapterTarget
import com.stepango.forma.target.DataBindingTarget
import com.stepango.forma.target.WidgetTarget
import com.stepango.forma.dependencies.applyDependencies
import com.stepango.forma.validation.validate
import com.stepango.forma.validation.validator
import com.stepango.forma.owner.Owner
import com.stepango.forma.owner.NoOwner
import com.stepango.forma.validation.disallowResources
import com.stepango.forma.visibility.Public
import com.stepango.forma.visibility.Visibility
import org.gradle.api.Project

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
    consumerMinificationFiles: Set<String> = emptySet() //TODO maybe default proguard files for DataBindings
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
    consumerMinificationFiles: Set<String> = emptySet() //TODO maybe default proguard files for DataBindings
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