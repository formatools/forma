import org.gradle.api.internal.artifacts.dependencies.DefaultImmutableVersionConstraint
import tools.forma.config.FormaSettingsStore
import tools.forma.deps.core.CustomConfiguration
import tools.forma.deps.core.NamedDependency

pluginManagement {
    repositories { google() }
    apply(
        from =
            "../build-settings/conventions/src/main/kotlin/convention-plugins.settings.gradle.kts"
    )
    includeBuild("../build-settings")
    includeBuild("../plugins")
    includeBuild("../includer")
}

plugins {
    id("convention-dependencies")
    id("tools.forma.includer")
    id("tools.forma.android")
}

includer { arbitraryBuildScriptNames = true }

rootProject.name = "application"

val filteredTokens =
    listOf(
        "com",
        "io",
        "net",
        "org",
        "gradle",
        "android",
        "androidx",
        "kotlin",
        "kotlinx",
        "google"
    )
val coilVersion = "2.1.0"
val sqliteVersion = "2.2.0"
val roomVersion = "2.5.1"

val ksp = CustomConfiguration("ksp")

fun projectDependencies(name: String = "libs", vararg deps: Any) {
    dependencyResolutionManagement {
        versionCatalogs {
            create(name) {
                deps.forEach {
                    when (it) {
                        is String -> addLibrary(it)
                        is BundleDep -> addBundle(it)
                    }
                }

                addPlugin("tools.forma.demo:dependencies", "0.0.1")
                addPlugin(
                    id = "com.google.devtools.ksp:symbol-processing-gradle-plugin",
                    version = "$embeddedKotlinVersion-1.0.10",
                    configuration = ksp,
                    "androidx.room:room-compiler:$roomVersion"
                )
            }
        }
    }
}

projectDependencies(
    "libs",
    "com.jakewharton.timber:timber:4.7.1",
    bundle(name = "coil", "io.coil-kt:coil:$coilVersion", "io.coil-kt:coil-base:$coilVersion"),
    bundle(
        name = "room",
        "androidx.sqlite:sqlite:$sqliteVersion",
        "androidx.sqlite:sqlite-framework:$sqliteVersion",
        "androidx.room:room-runtime:$roomVersion",
        "androidx.room:room-ktx:$roomVersion",
        "androidx.room:room-common:$roomVersion",
    ),
    // PluginDep()
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

class PluginDep(
    id: String,
    version: String,
    configuration: CustomConfiguration? = null,
    vararg dependencies: String,
    nameGenerator: (String) -> String = ::pluginNameGenerator,
    depNameGenerator: (String) -> String = ::defaultNameGenerator
)

data class PluginDependencyImpl(val id: String, val ver: VersionConstraint) : PluginDependency {
    override fun getPluginId(): String = id

    override fun getVersion(): VersionConstraint = ver
}

fun VersionCatalogBuilder.addPlugin(
    id: String,
    version: String,
    configuration: CustomConfiguration? = null,
    vararg dependencies: String,
    nameGenerator: (String) -> String = ::pluginNameGenerator,
    depNameGenerator: (String) -> String = ::defaultNameGenerator
) {
    val name = nameGenerator(id)
    plugin(name, id).version { strictly(version) }
    dependencies.forEach { dep ->
        if (configuration != null) {
            addLibrary(dep) { "${depNameGenerator(it)}.${configuration.name}" }
            FormaSettingsStore.registerConfiguration(
                configuration.name,
                providers.provider {
                    PluginDependencyImpl(id, DefaultImmutableVersionConstraint(version))
                },
                dep
            )
        } else {
            addLibrary(dep, depNameGenerator)
        }
    }
}

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

// todo split lib name and version
fun VersionCatalogBuilder.addLibrary(
    dependency: Any,
    nameGenerator: (String) -> String = ::defaultNameGenerator
): String {
    return when (dependency) {
        is String -> {
            val (group, artifact, version) = dependency.split(":")
            nameGenerator(dependency).apply {
                library(this, group, artifact).version { strictly(version) }
            }
        }
        is NamedDependency -> {
            val name = dependency.names.first().name
            val (group, artifact, version) = name.split(":")
            nameGenerator(name).apply {
                library(this, group, artifact).version { strictly(version) }
            }
        }
        else ->
            throw IllegalArgumentException(
                "Dependency type ${dependency::class.qualifiedName} is not supported"
            )
    }
}

fun pluginNameGenerator(groupArtifactVersion: String) =
    groupArtifactVersion
        .split(":")
        .fold(emptyList<String>()) { acc, s -> acc + s.split(".", "-") }
        .filter { it !in filteredTokens }
        .distinct()
        .joinToString(".")
        .also { println("Generated name $it for $groupArtifactVersion") }

fun defaultNameGenerator(groupArtifactVersion: String) =
    groupArtifactVersion
        .split(":")
        .dropLast(1)
        .fold(emptyList<String>()) { acc, s -> acc + s.split(".", "-") }
        .filter { it !in filteredTokens }
        .distinct()
        .joinToString(".")
        .also { println("Generated name $it for $groupArtifactVersion") }

// refer to this issue https://github.com/gradle/gradle/issues/18536
// tools.forma.dependencies are applied in buildscript {} block
includeBuild("../build-dependencies")
