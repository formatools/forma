import com.stepango.forma.feature.AndroidLibraryFeatureConfiguration
import com.stepango.forma.feature.androidLibraryFeatureDefinition
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinAndroidFeatureDefinition
import com.stepango.forma.feature.kotlinKaptFeatureDefinition
import com.stepango.forma.module.AndroidUtilModule
import com.stepango.forma.module.DataBindingAdapterModule
import com.stepango.forma.module.DataBindingModule
import com.stepango.forma.module.WidgetModule
import com.stepango.forma.utils.applyDependencies
import com.stepango.forma.validation.validate
import com.stepango.forma.validation.validator
import com.stepango.forma.owner.Owner
import com.stepango.forma.owner.NoOwner
import org.gradle.api.Project

// Only layouts allowed
fun Project.dataBinding(
    packageName: String,
    owner: Owner = NoOwner,
    dependencies: FormaDependency = emptyDependency(),
    consumerMinificationFiles: Set<String> = emptySet() //TODO maybe default proguard files for DataBindings
) {
    if (!Forma.configuration.dataBinding){
        throw IllegalArgumentException("Please enable dataBinding feature trough androidProjectConfiguration")
    }
    validate(DataBindingModule)
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
        validator = validator(WidgetModule, AndroidUtilModule, DataBindingAdapterModule),
        dependencies = dependencies
    )
}

// Only files with adapters allowed
fun Project.dataBindingAdapters(
    packageName: String,
    dependencies: FormaDependency = emptyDependency(),
    consumerMinificationFiles: Set<String> = emptySet() //TODO maybe default proguard files for DataBindings
) {
    if (!Forma.configuration.dataBinding){
        throw IllegalArgumentException("Please enable dataBinding feature trough androidProjectConfiguration")
    }
    validate(DataBindingAdapterModule)
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
        validator = validator(WidgetModule, AndroidUtilModule),
        dependencies = dependencies
    )
}