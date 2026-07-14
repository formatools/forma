package tools.forma.bazel.model

/**
 * Portable, Gradle-free snapshot of a Forma project for the Bazel adapter.
 * This is the input contract for generate/check. No Gradle Project, no AGP.
 *
 * See docs/BAZEL-ADAPTER.md §6 for rationale.
 */
data class FormaProjectModel(
    val workspaceName: String,
    val targets: List<TargetSnapshot>,
    // For spike we reconstruct / re-register via JvmBazelKit rather than snapshotting the full graph.
    // The field is kept for future extensibility (e.g. full graph export or Android matrix).
    val restrictionGraphSnapshot: RestrictionGraphSnapshot? = null,
)

/**
 * Minimal snapshot of one target declaration (project edge only for v1).
 * gradlePath uses Gradle colon form (e.g. ":feature:greeter:impl") for easy round-tripping.
 * The adapter normalizes to Bazel package + label.
 */
data class TargetSnapshot(
    val gradlePath: String,           // ":feature:greeter:impl" or ":binary"
    val typeId: String,               // "jvm.impl", "jvm.binary", ...
    val packageName: String? = null,  // informational (source root); not used for labels
    val dependencies: List<DepRef> = emptyList(),
    val testDependencies: List<DepRef> = emptyList(),
    val metadata: Map<String, String> = emptyMap(), // "mainClass" for binary
)

/** Reference to another target by its Gradle path (normalized to labels at generate time). */
data class DepRef(val path: String)

/**
 * Placeholder for richer graph export. In F-041 spike the adapter re-creates the JVM
 * restriction matrix from core types (see JvmBazelKit) rather than relying on this.
 */
data class RestrictionGraphSnapshot(
    val rules: List<RuleSnapshot> = emptyList(),
)

data class RuleSnapshot(
    val consumerTypeId: String,
    val allowedDepTypeIds: List<String>,
)
