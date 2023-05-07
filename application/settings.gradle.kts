pluginManagement {
    apply(from = "../build-settings/conventions/src/main/kotlin/convention-plugins.settings.gradle.kts")
    includeBuild("../build-settings")
    includeBuild("../plugins")
    includeBuild("../includer")
    includeBuild("../depgen")
}

plugins {
    id("convention-dependencies")
    id("tools.forma.includer")
    id("tools.forma.android")
    id("tools.forma.deps")
    id("tools.forma.depgen")
}

rootProject.name = "application"

val filteredTokens = listOf("com", "io", "net", "org")
val coilVersion = "2.1.0"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            addLibrary("com.jakewharton.timber:timber:4.7.1")
            addBundle("coil", "io.coil-kt:coil:$coilVersion", "io.coil-kt:coil-base:$coilVersion")
        }
    }
}

fun VersionCatalogBuilder.addBundle(
    name: String,
    vararg groupArtifactVersion: String,
    nameGenerator: (String) -> String = ::defaultNameGenerator
): String {
    bundle(name, groupArtifactVersion.map { addLibrary(it, nameGenerator) })
    return name
}

fun VersionCatalogBuilder.addLibrary(
    groupArtifactVersion: String,
    nameGenerator: (String) -> String = ::defaultNameGenerator
): String {
    val (group, artifact, version) = groupArtifactVersion.split(":")
    return nameGenerator(groupArtifactVersion).apply {
        library(this, group, artifact).version { strictly(version) }
    }
}

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
