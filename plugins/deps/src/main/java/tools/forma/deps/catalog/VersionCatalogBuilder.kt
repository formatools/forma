package tools.forma.deps.catalog

import org.gradle.api.artifacts.VersionConstraint
import org.gradle.api.initialization.Settings
import org.gradle.api.initialization.dsl.VersionCatalogBuilder
import org.gradle.api.internal.artifacts.dependencies.DefaultImmutableVersionConstraint
import org.gradle.plugin.use.PluginDependency
import tools.forma.config.FormaSettingsStore
import tools.forma.deps.core.CustomConfiguration
import tools.forma.deps.core.NamedDependency

fun Settings.projectDependencies(name: String = "libs", vararg deps: Any) {
    dependencyResolutionManagement {
        it.versionCatalogs { container ->
            container.create(name) { builder ->
                deps.forEach { dep ->
                    when (dep) {
                        is String -> builder.addLibrary(settings, dep)
                        is BundleDep -> builder.addBundle(settings, dep)
                        is PluginDep -> builder.addPlugin(settings, dep)
                    }
                }
            }
        }
    }
}


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
            addLibrary(settings, dep) { depNameGenerator(it) }
            FormaSettingsStore.registerConfiguration(
                configuration.name,
                settings.providers.provider {
                    PluginDependencyImpl(id, DefaultImmutableVersionConstraint(version))
                },
                dep
            )
        } else {
            addLibrary(settings, dep, depNameGenerator)
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

fun VersionCatalogBuilder.addBundle(settings: Settings, bundle: BundleDep) {
    addBundle(settings, bundle.name, *bundle.groupArtifactVersions, nameGenerator = bundle.nameGenerator)
}

// todo split lib name and version
fun VersionCatalogBuilder.addBundle(
    settings: Settings,
    name: String,
    vararg groupArtifactVersion: Any,
    nameGenerator: (String) -> String = ::defaultNameGenerator
) {
    bundle(name, groupArtifactVersion.map { addLibrary(settings, it, nameGenerator) })
}

// todo split lib name and version
fun VersionCatalogBuilder.addLibrary(
    settings: Settings,
    dependency: Any,
    nameGenerator: (String) -> String = ::defaultNameGenerator
): String {
    return when (dependency) {
        is String -> {
            val (group, artifact, version) = dependency.split(":")
            nameGenerator(dependency).apply {
                library(this, group, artifact).version { it.strictly(version) }
            }
        }
        is NamedDependency -> {
            val name = dependency.names.first().name
            val (group, artifact, version) = name.split(":")
            nameGenerator(name).apply {
                library(this, group, artifact).version { it.strictly(version) }
            }
        }
        else ->
            throw IllegalArgumentException(
                "Dependency type ${dependency::class.qualifiedName} is not supported"
            )
    }
}
