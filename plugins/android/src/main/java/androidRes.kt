import org.gradle.api.Project
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.deps.core.FormaDependency
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner

// Only resources allowed. Pre-defined `res` type — no external plugins by default.
// For safe-args / selective plugins, use a Path B derived type (see docs/TARGET-PLUGINS.md).
fun Project.androidRes(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {
    resourcesTarget(
        type = AndroidTargetTypes.res,
        packageName = packageName,
        owner = owner,
        visibility = visibility,
        dependencies = dependencies,
        manifestPlaceholders = manifestPlaceholders,
    )
}
