package tools.forma.android.utils

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.ProductFlavor

/**
 * Typed product-flavor identity for Forma Android targets (F-115 / NiA F7 + F27).
 *
 * **Same model for binary and library:** [FormaProductFlavor] is the one global way
 * to declare flavor dimensions. Composition-root APKs use it on `androidBinary`;
 * library targets that go through [tools.forma.android.feature.androidLibraryFeatureDefinition]
 * (e.g. `androidUtil`) accept the same list.
 *
 * Libraries **ignore** APK-only fields ([applicationIdSuffix], [versionNameSuffix]) —
 * [applyFormaProductFlavor] only writes those onto [ApplicationProductFlavor].
 * [BuildConfiguration] stays build-types only (flavors are not stuffed into it).
 *
 * Empty list (default) = unflavored target so existing samples stay unchanged.
 * Flavor-scoped external deps use `NamedDependency.forProductFlavor` /
 * `PlatformDependency.forProductFlavor` → AGP `prodImplementation` etc.
 * (see `docs/CALL-SITE-SURFACE.md`).
 *
 * @param name flavor name (`demo`, `prod`, …)
 * @param dimension flavor dimension name (`contentType`, …)
 * @param applicationIdSuffix optional APK id suffix (e.g. `".demo"`); null = none;
 *   ignored on library targets
 * @param versionNameSuffix optional versionName suffix; null = none; ignored on libraries
 * @param matchingFallbacks optional AGP matchingFallbacks for this flavor
 * @param manifestPlaceholders extra placeholders merged onto this flavor
 * @param buildConfigFields optional BuildConfig field map (name → value expression)
 *   applied only when project-global `buildFeatures.buildConfig` is already on —
 *   Forma does not silently enable BuildConfig from flavors
 */
data class FormaProductFlavor(
    val name: String,
    val dimension: String,
    val applicationIdSuffix: String? = null,
    val versionNameSuffix: String? = null,
    val matchingFallbacks: List<String> = emptyList(),
    val manifestPlaceholders: Map<String, Any> = emptyMap(),
    val buildConfigFields: Map<String, BuildConfigField> = emptyMap(),
) {
    init {
        require(name.isNotBlank()) { "FormaProductFlavor.name must not be blank" }
        require(dimension.isNotBlank()) { "FormaProductFlavor.dimension must not be blank" }
    }
}

/** Typed BuildConfig field for [FormaProductFlavor.buildConfigFields]. */
data class BuildConfigField(
    val type: String,
    val value: String,
) {
    init {
        require(type.isNotBlank()) { "BuildConfigField.type must not be blank" }
        require(value.isNotBlank()) { "BuildConfigField.value must not be blank" }
    }
}

/**
 * Pure plan for AGP apply: ordered unique dimensions + flavors.
 *
 * @throws IllegalArgumentException on blank names or duplicate flavor names
 */
fun resolveProductFlavorPlan(
    flavors: List<FormaProductFlavor>,
): ProductFlavorPlan {
    if (flavors.isEmpty()) return ProductFlavorPlan.EMPTY
    val names = mutableSetOf<String>()
    val dimensions = linkedSetOf<String>()
    flavors.forEach { flavor ->
        require(flavor.name !in names) {
            "Duplicate product flavor name '${flavor.name}' " +
                "(each name may appear once per target)"
        }
        names += flavor.name
        dimensions += flavor.dimension
    }
    return ProductFlavorPlan(
        dimensions = dimensions.toList(),
        flavors = flavors,
    )
}

/** Ordered dimension names + flavor list ready for AGP containers. */
data class ProductFlavorPlan(
    val dimensions: List<String>,
    val flavors: List<FormaProductFlavor>,
) {
    companion object {
        val EMPTY = ProductFlavorPlan(emptyList(), emptyList())
    }
}

/**
 * Applies [ProductFlavorPlan] onto AGP [ApplicationExtension] flavor containers.
 *
 * No-op when the plan is empty (default `androidBinary` call sites).
 */
fun ApplicationExtension.applyProductFlavors(plan: ProductFlavorPlan) {
    if (plan.flavors.isEmpty()) return
    flavorDimensions.clear()
    flavorDimensions.addAll(plan.dimensions)
    plan.flavors.forEach { forma ->
        val existing = productFlavors.findByName(forma.name)
        val flavor = existing ?: productFlavors.create(forma.name)
        applyFormaProductFlavor(flavor, forma)
    }
}

/**
 * Applies [ProductFlavorPlan] onto AGP [LibraryExtension] flavor containers and
 * registers `src/<flavor>/kotlin` on each flavor source set.
 *
 * No-op when the plan is empty (default library call sites). Does **not** require
 * [ApplicationProductFlavor] fields — APK-only attrs are skipped by
 * [applyFormaProductFlavor].
 */
fun LibraryExtension.applyProductFlavors(plan: ProductFlavorPlan) {
    if (plan.flavors.isEmpty()) return
    flavorDimensions.clear()
    flavorDimensions.addAll(plan.dimensions)
    plan.flavors.forEach { forma ->
        val existing = productFlavors.findByName(forma.name)
        val flavor = existing ?: productFlavors.create(forma.name)
        applyFormaProductFlavor(flavor, forma)
        // Kotlin sources under src/<flavor>/kotlin (parity with main/test wiring).
        sourceSets.maybeCreate(forma.name).java.srcDir("src/${forma.name}/kotlin")
    }
}

/** Writes one [FormaProductFlavor] onto an AGP [ProductFlavor] / application flavor. */
fun applyFormaProductFlavor(target: ProductFlavor, forma: FormaProductFlavor) {
    target.dimension = forma.dimension
    if (target is ApplicationProductFlavor) {
        forma.applicationIdSuffix?.let { target.applicationIdSuffix = it }
        forma.versionNameSuffix?.let { target.versionNameSuffix = it }
    }
    if (forma.matchingFallbacks.isNotEmpty()) {
        target.setMatchingFallbacks(forma.matchingFallbacks)
    }
    if (forma.manifestPlaceholders.isNotEmpty()) {
        target.manifestPlaceholders.putAll(forma.manifestPlaceholders)
    }
    forma.buildConfigFields.forEach { (fieldName, field) ->
        target.buildConfigField(field.type, fieldName, field.value)
    }
}

/**
 * Convenience: resolve + apply in one step for binary feature definition.
 * Empty [flavors] is a no-op (keeps unflavored APKs working).
 */
fun ApplicationExtension.applyProductFlavors(flavors: List<FormaProductFlavor>) {
    applyProductFlavors(resolveProductFlavorPlan(flavors))
}

/**
 * Convenience: resolve + apply in one step for library feature definition.
 * Empty [flavors] is a no-op (keeps unflavored libraries working).
 */
fun LibraryExtension.applyProductFlavors(flavors: List<FormaProductFlavor>) {
    applyProductFlavors(resolveProductFlavorPlan(flavors))
}
