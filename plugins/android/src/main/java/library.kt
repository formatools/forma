import tools.forma.android.target.AndroidTargetTypes
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.deps.core.FormaDependency

/**
 * Can't depend on api\impl
 *
 * Path B derived JVM library kinds (Protobuf, etc.) use [libraryTarget] with a derived type.
 */
fun Project.library(
    packageName: String,
    dependencies: FormaDependency = emptyDependency(),
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    testDependencies: FormaDependency = emptyDependency()
) {
    libraryTarget(
        type = AndroidTargetTypes.jvmLibrary,
        packageName = packageName,
        dependencies = dependencies,
        owner = owner,
        visibility = visibility,
        testDependencies = testDependencies,
    )
}
