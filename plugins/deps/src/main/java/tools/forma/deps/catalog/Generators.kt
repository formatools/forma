package tools.forma.deps.catalog

import java.util.Locale
import org.gradle.configurationcache.extensions.capitalized

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
        "google",
        "plugin"
    )

fun pluginNameGenerator(groupArtifactVersion: String) =
    groupArtifactVersion
        .split(":")
        .fold(emptyList<String>()) { acc, s -> acc + s.split(".", "-") }
        .filter { it !in filteredTokens }
        .distinct()
        .joinToString("") { it.capitalized() }
        .let { name -> name.replaceFirstChar { it.lowercase(Locale.getDefault()) } }
        .also { println("Generated name $it for $groupArtifactVersion") }

fun defaultNameGenerator(groupArtifactVersion: String) =
    groupArtifactVersion
        .split(":")
        .dropLast(1)
        .fold(emptyList<String>()) { acc, s -> acc + s.split(".", "-") }
        .filter { it !in filteredTokens }
        .distinct()
        .joinToString("") { it.capitalized() }
        .let { name -> name.replaceFirstChar { it.lowercase(Locale.getDefault()) } }
        .also { println("Generated name $it for $groupArtifactVersion") }
