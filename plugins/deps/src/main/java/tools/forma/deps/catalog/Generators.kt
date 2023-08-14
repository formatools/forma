package tools.forma.deps.catalog

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
