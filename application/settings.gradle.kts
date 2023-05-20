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

val filteredTokens = listOf("com", "io", "net", "org", "gradle")
val coilVersion = "2.1.0"
val sqliteVersion = "2.2.0"
val roomVersion = "2.5.1"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            addLibrary("com.jakewharton.timber:timber:4.7.1")
            addBundle(name = "coil", "io.coil-kt:coil:$coilVersion", "io.coil-kt:coil-base:$coilVersion")
            addBundle(
                name = "room",
                "androidx.sqlite:sqlite:$sqliteVersion",
                "androidx.sqlite:sqlite-framework:$sqliteVersion",
                "androidx.room:room-runtime:$roomVersion",
                "androidx.room:room-ktx:$roomVersion",
                "androidx.room:room-common:$roomVersion",
            )
            addPlugin("tools.forma.demo:dependencies:0.0.1")
            addPlugin("com.google.devtools.ksp:symbol-processing-gradle-plugin:1.8.10-1.0.9", "androidx.room:room-compiler:$roomVersion")
        }

    }
}

fun VersionCatalogBuilder.addPlugin(
    groupArtifactVersion: String,
    vararg dependencies: String,
    nameGenerator: (String) -> String = ::defaultNameGenerator
) {
    val (group, artifact, version) = groupArtifactVersion.split(":")
    plugin(nameGenerator(groupArtifactVersion), "$group:$artifact").version { strictly(version) }
}

fun VersionCatalogBuilder.addBundle(
    name: String,
    vararg groupArtifactVersion: String,
    nameGenerator: (String) -> String = ::defaultNameGenerator
) {
    bundle(name, groupArtifactVersion.map { addLibrary(it, nameGenerator) })
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
        .verifyVersion()
        .dropLast(1)
        .fold(emptyList<String>()) { acc, s -> acc + s.split(".", "-") }
        .filter { it !in filteredTokens }
        .distinct()
        .joinToString(".")
        .also { println("Generated name $it for $groupArtifactVersion") }

// refer to this issue https://github.com/gradle/gradle/issues/18536
// tools.forma.dependencies are applied in buildscript {} block
includeBuild("../build-dependencies")

fun List<String>.verifyVersion(): List<String> {
    // Very naive check if last string contains version
    if (!last().last().isDigit()) throw IllegalArgumentException("Version is not specified")
    return this
}
