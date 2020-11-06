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
import com.stepango.forma.visibility.Public
import com.stepango.forma.visibility.Visibility
import org.gradle.api.Project
import java.lang.IllegalStateException

// Only resources allowed
fun Project.androidRes(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {
    file("./src/main").listFiles()
        ?.filter { it.isDirectory }
        ?.map { it.name }
        ?.apply {
            assert(size == 1)
            assert(first() == "res")
        } ?: throw IllegalStateException("No resources folder found")

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