import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.ResourcesTarget
import tools.forma.android.owner.NoOwner
import tools.forma.android.owner.Owner
import tools.forma.android.validation.validate
import tools.forma.android.validation.validator
import java.io.File
import tools.forma.android.validation.validateDirectoryContent
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.android.dependencies.applyDependencies

// Only resources allowed
fun Project.androidRes(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {

    validateDirectoryContent(
        dir = "./src/main",
        errorMsg = "Please make sure this target only contains `res` folder in `src/main`"
    ) {
        it.filter(File::isDirectory)
            .run { size == 1 && first().name == "res" }
    }

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
        validator = validator(ResourcesTarget),
        dependencies = dependencies
    )
}