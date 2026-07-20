import org.gradle.api.Project
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.android.target.deriveTargetType
import tools.forma.android.target.targetPlugin
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.deps.core.FormaDependency
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner

// F-072: Sample Path B derived target for modules that need the safe-args plugin.
// Registered at class load (before call sites execute) via top-level val.
// nameSuffix kept as "res" so SuffixNameMatcher + existing consumers continue to work
// without touching the dependency matrix or directory names.
private val navigationSafeArgs = targetPlugin(id = "androidx.navigation.safeargs.kotlin")

val NavigationResType = deriveTargetType(
    id = "sample.navigation-res",
    base = AndroidTargetTypes.res,
    nameSuffix = "res",
    plugins = listOf(navigationSafeArgs),
)

/**
 * Resources target that carries the androidx.navigation.safeargs.kotlin plugin (Path B).
 * Call sites are Bazel-flat: only attributes, no plugin ids.
 *
 * Delegates to [resourcesTarget] with [NavigationResType] so type-owned plugins auto-apply.
 */
fun Project.navigationRes(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {
    resourcesTarget(
        type = NavigationResType,
        packageName = packageName,
        owner = owner,
        visibility = visibility,
        dependencies = dependencies,
        manifestPlaceholders = manifestPlaceholders,
    )
}
