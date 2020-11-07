import com.stepango.forma.dependencies.applyDependencies
import com.stepango.forma.feature.AndroidLibraryFeatureConfiguration
import com.stepango.forma.feature.androidLibraryFeatureDefinition
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinAndroidFeatureDefinition
import com.stepango.forma.target.ResourcesTarget
import com.stepango.forma.owner.NoOwner
import com.stepango.forma.owner.Owner
import com.stepango.forma.validation.validate
import com.stepango.forma.validation.validator
import java.io.File
import com.stepango.forma.validation.validateDirectoryContent
import com.stepango.forma.visibility.Public
import com.stepango.forma.visibility.Visibility
import org.gradle.api.Project

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