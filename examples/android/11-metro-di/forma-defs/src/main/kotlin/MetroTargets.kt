import org.gradle.api.Project
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.android.target.registerTargetPlugin
import tools.forma.android.target.targetPlugin
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.deps.core.FormaDependency
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner

/**
 * F-095 progressive example — Metro DI via type-owned plugins.
 *
 * Registers Metro on the pre-defined `impl` and `app` kinds (Path A), then exposes
 * thin `metroImpl` / `metroApp` DSLs so call sites document intent and force the
 * registration class to load before [impl] / [androidApp] run.
 *
 * There is no public `implTarget(type=…)` helper yet (unlike [resourcesTarget] for
 * res Path B). Path A + thin DSL keeps call sites attributes-only and avoids
 * forking AGP wiring in every example.
 *
 * Metro plugin id: `dev.zacsweers.metro` (Kotlin compiler plugin — not KSP).
 */
private val metroCompiler = targetPlugin(id = "dev.zacsweers.metro")

/** Side-effecting load: bind Metro to impl + app once. */
internal object MetroPluginBindings {
    init {
        registerTargetPlugin(AndroidTargetTypes.impl, metroCompiler)
        registerTargetPlugin(AndroidTargetTypes.app, metroCompiler)
    }
}

/**
 * Feature impl that compiles with Metro. Attributes-only call site.
 * Prefer this over bare `impl` in Metro modules so registration is loaded.
 */
fun Project.metroImpl(
    packageName: String,
    dependencies: FormaDependency = emptyDependency(),
) {
    MetroPluginBindings
    impl(
        packageName = packageName,
        dependencies = dependencies,
    )
}

/**
 * Composition-root app library that compiles with Metro (`@DependencyGraph`).
 * Still AGP library shell — APK identity stays on [androidBinary].
 */
fun Project.metroApp(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
) {
    MetroPluginBindings
    androidApp(
        packageName = packageName,
        owner = owner,
        visibility = visibility,
        dependencies = dependencies,
    )
}
