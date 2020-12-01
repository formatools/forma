import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.ResourcesTarget
import tools.forma.android.target.WidgetTarget
import tools.forma.android.owner.NoOwner
import tools.forma.android.owner.Owner
import tools.forma.validation.validate
import tools.forma.validation.validator
import tools.forma.android.validation.onlyAllowResources
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.android.dependencies.applyDependencies
import tools.forma.deps.FormaDependency

// Only resources allowed
fun Project.androidRes(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {

    onlyAllowResources()

    validate(ResourcesTarget)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName = packageName,
        manifestPlaceholders = manifestPlaceholders
    )
    applyFeatures(
        androidLibraryFeatureDefinition(libraryFeatureConfiguration),
        kotlinAndroidFeatureDefinition()
    )

    applyDependencies(
        validator = validator(
            ResourcesTarget,
            WidgetTarget
        ),
        dependencies = dependencies,
        repositoriesConfiguration = Forma.configuration.repositories
    )
}