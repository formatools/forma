import org.gradle.api.Project
import tools.forma.android.feature.AndroidLibraryFeatureConfiguration
import tools.forma.android.feature.androidLibraryFeatureDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kaptConfigurationFeature
import tools.forma.android.feature.kotlinAndroidFeatureDefinition
import tools.forma.android.target.AndroidTargetRegistry
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.android.utils.BuildConfiguration
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.NamedDependency
import tools.forma.deps.core.applyDependencies
import tools.forma.validation.asValidator
import tools.forma.validation.validate

/**
 * **Deprecated.** Generic Android library target — a temporary escape hatch that
 * flattens Forma's typed structure back into "just an Android library".
 *
 * Prefer a **role-specific** target so the dependency matrix can protect a flat
 * graph:
 * - shared Android helpers (no `res/`) → [androidUtil]
 * - shared UI bases for impl/widget → [uiLibrary]
 * - resources only → [androidRes]
 * - layouts only → [viewBinding]
 * - feature UI/DI → [impl]
 * - pure JVM → [library]
 *
 * Remains functional until hard-removal (F-063) so existing consumers can migrate.
 */
@Deprecated(
    message = "androidLibrary is a temporary generic escape hatch. Use androidUtil, " +
        "uiLibrary, androidRes, viewBinding, impl, or library so Forma can keep structure flat.",
    replaceWith = ReplaceWith("androidUtil(packageName, owner, visibility, dependencies, testDependencies, compose)")
)
fun Project.androidLibrary(
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    testInstrumentationRunner: String = androidJunitRunner,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap(),
    /** Enable Jetpack Compose; defaults to project-wide `compose` setting. */
    compose: Boolean = Forma.settings.compose,
): TargetBuilder {
    AndroidTargetRegistry.selfValidator(AndroidTargetTypes.library).asValidator().validate(target)
    val libraryFeatureConfiguration = AndroidLibraryFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
        compose = compose,
    )
    applyFeatures(
        androidLibraryFeatureDefinition(libraryFeatureConfiguration),
        kotlinAndroidFeatureDefinition()
    )

    applyDependencies(
        validator = AndroidTargetRegistry.validatorFor(AndroidTargetTypes.library).asValidator(),
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies,
        configurationFeatures = kaptConfigurationFeature()
    )

    return TargetBuilder(this)
}
