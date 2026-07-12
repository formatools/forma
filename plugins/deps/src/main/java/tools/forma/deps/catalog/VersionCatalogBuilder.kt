package tools.forma.deps.catalog

import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.dsl.VersionCatalogBuilder
import org.gradle.api.internal.artifacts.dependencies.DefaultImmutableVersionConstraint
import org.gradle.plugin.use.PluginDependency
import tools.forma.config.FormaSettingsStore
import tools.forma.deps.core.CustomConfiguration
import tools.forma.deps.core.NamedDependency

/**
 * Declares a Gradle version catalog from Forma settings.
 *
 * Supported entries:
 * - bare `String` coordinates → library with [defaultNameGenerator]
 * - [LibraryDep] via [library] → library with optional explicit name
 * - [BundleDep] via [bundle] → named bundle of libraries
 * - [PluginDep] via [plugin] → plugin + optional companion libraries / custom configuration
 *
 * Example:
 * ```
 * projectDependencies(
 *     "libs",
 *     "com.jakewharton.timber:timber:5.0.1",
 *     library("io.coil-kt:coil:2.1.0", name = "coil"),
 *     bundle(name = "room", "androidx.room:room-runtime:2.5.1", ...),
 *     plugin(
 *         id = "com.google.devtools.ksp:symbol-processing-gradle-plugin",
 *         version = "…",
 *         configuration = CustomConfiguration("ksp"),
 *         "androidx.room:room-compiler:2.5.1"
 *     )
 * )
 * // → libs.jakewhartonTimber, libs.coil, libs.bundles.room, libs.plugins.…
 * ```
 *
 * @see docs/DEPS-CATALOG.md
 */
fun Settings.projectDependencies(name: String = "libs", vararg deps: Any) {
    dependencyResolutionManagement {
        it.versionCatalogs { container ->
            container.create(name) { builder ->
                deps.forEach { dep ->
                    when (dep) {
                        is String -> builder.addLibrary(dep)
                        is LibraryDep -> builder.addLibrary(dep)
                        is BundleDep -> builder.addBundle(dep)
                        is PluginDep -> builder.addPlugin(this@projectDependencies, dep)
                        else ->
                            throw IllegalArgumentException(
                                "Unsupported projectDependencies entry " +
                                    "${dep::class.qualifiedName}. Use a GAV String, library(), " +
                                    "bundle(), or plugin()."
                            )
                    }
                }
            }
        }
    }
}

/**
 * Explicit library entry. Prefer this over a bare String when the auto-generated accessor name
 * would be unclear or when you want a stable short name (e.g. `libs.coil` instead of
 * `libs.coilKt`).
 */
fun library(
    groupArtifactVersion: String,
    name: String? = null,
    nameGenerator: (String) -> String = ::defaultNameGenerator
) = LibraryDep(groupArtifactVersion, name, nameGenerator)

class LibraryDep(
    val groupArtifactVersion: String,
    val name: String? = null,
    val nameGenerator: (String) -> String = ::defaultNameGenerator
)

fun bundle(
    name: String,
    vararg groupArtifactVersions: Any,
    nameGenerator: (String) -> String = ::defaultNameGenerator
) = BundleDep(name, groupArtifactVersions, nameGenerator)

class BundleDep(
    val name: String,
    val groupArtifactVersions: Array<out Any>,
    val nameGenerator: (String) -> String = ::defaultNameGenerator
)

fun plugin(
    id: String,
    version: String,
    configuration: CustomConfiguration? = null,
    vararg dependencies: String,
    nameGenerator: (String) -> String = ::pluginNameGenerator,
    depNameGenerator: (String) -> String = ::defaultNameGenerator
) = PluginDep(id, version, configuration, dependencies, nameGenerator, depNameGenerator)

class PluginDep(
    val id: String,
    val version: String,
    val configuration: CustomConfiguration? = null,
    val dependencies: Array<out String>,
    val nameGenerator: (String) -> String = ::pluginNameGenerator,
    val depNameGenerator: (String) -> String = ::defaultNameGenerator
)

data class PluginDependencyImpl(val id: String, val ver: VersionConstraint) : PluginDependency {
    override fun getPluginId(): String = id

    override fun getVersion(): VersionConstraint = ver
}

fun VersionCatalogBuilder.addPlugin(
    settings: Settings,
    id: String,
    version: String,
    configuration: CustomConfiguration? = null,
    vararg dependencies: String,
    nameGenerator: (String) -> String = ::pluginNameGenerator,
    depNameGenerator: (String) -> String = ::defaultNameGenerator
) {
    val name = nameGenerator(id)
    plugin(name, id).version { it.strictly(version) }
    dependencies.forEach { dep ->
        if (configuration != null) {
            addLibrary(dep) { depNameGenerator(it) }
            FormaSettingsStore.registerConfiguration(
                configuration.name,
                settings.providers.provider {
                    PluginDependencyImpl(id, DefaultImmutableVersionConstraint(version))
                },
                dep
            )
        } else {
            addLibrary(dep, depNameGenerator)
        }
    }
}

fun VersionCatalogBuilder.addPlugin(settings: Settings, pluginDep: PluginDep) =
    addPlugin(
        settings,
        pluginDep.id,
        pluginDep.version,
        pluginDep.configuration,
        *pluginDep.dependencies,
        nameGenerator = pluginDep.nameGenerator,
        depNameGenerator = pluginDep.depNameGenerator
    )

fun VersionCatalogBuilder.addBundle(bundle: BundleDep) {
    addBundle(bundle.name, *bundle.groupArtifactVersions, nameGenerator = bundle.nameGenerator)
}

// todo split lib name and version
fun VersionCatalogBuilder.addBundle(
    name: String,
    vararg groupArtifactVersion: Any,
    nameGenerator: (String) -> String = ::defaultNameGenerator
) {
    bundle(name, groupArtifactVersion.map { addLibrary(it, nameGenerator) })
}

fun VersionCatalogBuilder.addLibrary(library: LibraryDep): String {
    val catalogName = library.name ?: library.nameGenerator(library.groupArtifactVersion)
    return registerLibrary(catalogName, library.groupArtifactVersion)
}

// todo split lib name and version
fun VersionCatalogBuilder.addLibrary(
    dependency: Any,
    nameGenerator: (String) -> String = ::defaultNameGenerator
): String {
    return when (dependency) {
        is String -> {
            val catalogName = nameGenerator(dependency)
            registerLibrary(catalogName, dependency)
        }
        is LibraryDep -> addLibrary(dependency)
        is NamedDependency -> {
            val notation = dependency.names.first().name
            val catalogName = nameGenerator(notation)
            registerLibrary(catalogName, notation)
        }
        else ->
            throw IllegalArgumentException(
                "Dependency type ${dependency::class.qualifiedName} is not supported. " +
                    "Use a GAV String, library(), or NamedDependency."
            )
    }
}

private fun VersionCatalogBuilder.registerLibrary(
    catalogName: String,
    notation: String
): String {
    val (group, artifact, version) = parseGroupArtifactVersion(notation)
    library(catalogName, group, artifact).version { it.strictly(version) }
    return catalogName
}
