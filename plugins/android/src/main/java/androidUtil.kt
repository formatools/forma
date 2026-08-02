import tools.forma.android.target.AndroidTargetTypes
import tools.forma.android.utils.FormaProductFlavor
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.deps.core.FormaDependency

/**
 * TODO
 *
 * External AAR Libraries extensions - usage:
 * Add libraries to dependencies e.g. appcompat
 * retrofit dependency cluster should include extension
 *
 * ```
 * val retrofit = transitiveDeps(
 *     "com.google.design:material:2.8.0"
 * ) + deps(
 *     project("material-android-util")
 * )
 * ```
 *
 * Path B derived util kinds (Room, etc.) use [androidUtilTarget] with a derived type.
 */
fun Project.androidUtil(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: FormaDependency = emptyDependency(),
    /** Enable Jetpack Compose; defaults to project-wide `compose` setting. */
    compose: Boolean = Forma.settings.compose,
    /**
     * Library product flavors (F-115 / NiA F27). Same [FormaProductFlavor] model as
     * `androidBinary`; empty = unflavored. Pair with `NamedDependency.forProductFlavor` /
     * `PlatformDependency.forProductFlavor` for AGP `prodImplementation` edges.
     */
    productFlavors: List<FormaProductFlavor> = emptyList(),
) {
    androidUtilTarget(
        type = AndroidTargetTypes.androidUtil,
        packageName = packageName,
        owner = owner,
        visibility = visibility,
        dependencies = dependencies,
        testDependencies = testDependencies,
        compose = compose,
        productFlavors = productFlavors,
    )
}
