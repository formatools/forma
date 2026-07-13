package tools.forma.core.target

/**
 * Stable identity for a target type used in restriction graphs and registry.
 * Platforms (Android, JVM, ...) own the concrete instances and their ids.
 */
interface TargetType {
    /** Stable id for registry, restriction rules and docs. e.g. "android.impl", "jvm.library". */
    val id: String

    /**
     * Gradle project name suffix used by default suffix-based name matching.
     * e.g. "impl", "library". Not necessarily unique across platforms (see forma-core-api §7).
     */
    val nameSuffix: String

    /** Optional human label for error messages. */
    val displayName: String get() = id
}

/** Lightweight reference to a configured target (name is sufficient for core matching). */
interface TargetRef {
    val name: String
}

/** Simple data implementation of TargetType. */
data class SimpleTargetType(
    override val id: String,
    override val nameSuffix: String,
    override val displayName: String = id
) : TargetType

/** Factory for TargetType (pure, no platform assumptions). */
fun targetType(
    id: String,
    nameSuffix: String,
    displayName: String = id
): TargetType = SimpleTargetType(id, nameSuffix, displayName)
