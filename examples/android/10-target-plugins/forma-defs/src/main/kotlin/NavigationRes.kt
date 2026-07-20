import org.gradle.api.Project
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.android.target.deriveTargetType
import tools.forma.android.target.targetPlugin
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.deps.core.FormaDependency
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner

/**
 * F-073 progressive example — Path B derived type.
 *
 * Plugin identity (safe-args) is owned by the type. Call sites stay attributes-only.
 * Registered at class load via top-level vals (same pattern as application sample).
 */
private val navigationSafeArgs = targetPlugin(id = "androidx.navigation.safeargs.kotlin")

val ExampleNavigationResType = deriveTargetType(
    id = "example.navigation-res",
    base = AndroidTargetTypes.res,
    nameSuffix = "res",
    plugins = listOf(navigationSafeArgs),
)

/**
 * Resources target that carries androidx.navigation.safeargs.kotlin (Path B).
 * No plugin ids at the call site.
 */
fun Project.navigationRes(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    manifestPlaceholders: Map<String, Any> = emptyMap(),
) {
    resourcesTarget(
        type = ExampleNavigationResType,
        packageName = packageName,
        owner = owner,
        visibility = visibility,
        dependencies = dependencies,
        manifestPlaceholders = manifestPlaceholders,
    )
}
