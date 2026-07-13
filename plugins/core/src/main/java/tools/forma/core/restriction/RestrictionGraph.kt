package tools.forma.core.restriction

import tools.forma.core.target.TargetType

/**
 * Kind of dependency edge. Enables future differentiation for test vs impl
 * project dependencies (current applyDependencies only suffix-validates main deps).
 */
enum class EdgeKind {
    IMPLEMENTATION,
    TEST,
    ANDROID_TEST
}

/**
 * A single restriction rule: what a consumer type may depend on for given edge kinds.
 */
data class RestrictionRule(
    val from: TargetType,
    val allowedDependencies: Set<TargetType>,
    val edgeKinds: Set<EdgeKind> = setOf(EdgeKind.IMPLEMENTATION)
)

/**
 * Read-only view of the restriction graph.
 */
interface RestrictionGraph {
    fun allowedTypes(consumer: TargetType, edge: EdgeKind = EdgeKind.IMPLEMENTATION): Set<TargetType>
    fun isAllowed(consumer: TargetType, dependency: TargetType, edge: EdgeKind = EdgeKind.IMPLEMENTATION): Boolean
    fun ruleFor(consumer: TargetType): RestrictionRule?
}

/**
 * Mutable builder for restriction rules. Used by platform kits (Android, JVM) to declare
 * their dependency matrix once at configuration time.
 *
 * Semantics:
 * - Closed world: only explicitly allowed edges pass isAllowed.
 * - Keyed by TargetType identity (unique id) per forma-core-api §3.3 and §7.
 * - impl ↛ impl is a critical rule (enforced by not listing it).
 */
class MutableRestrictionGraph : RestrictionGraph {
    private val rules: MutableMap<TargetType, RestrictionRule> = mutableMapOf()

    fun allow(from: TargetType, vararg to: TargetType): MutableRestrictionGraph =
        allow(from, to.asList())

    fun allow(from: TargetType, to: Collection<TargetType>): MutableRestrictionGraph {
        val existing = rules[from]
        val merged = (existing?.allowedDependencies ?: emptySet()) + to.toSet()
        rules[from] = RestrictionRule(
            from = from,
            allowedDependencies = merged,
            edgeKinds = existing?.edgeKinds ?: setOf(EdgeKind.IMPLEMENTATION)
        )
        return this
    }

    /**
     * Optional: declare allowed edge kinds for a consumer (defaults to IMPLEMENTATION).
     */
    fun allowEdges(from: TargetType, vararg edges: EdgeKind): MutableRestrictionGraph {
        val existing = rules[from]
        val allowed = existing?.allowedDependencies ?: emptySet()
        rules[from] = RestrictionRule(from, allowed, edges.toSet())
        return this
    }

    override fun allowedTypes(consumer: TargetType, edge: EdgeKind): Set<TargetType> {
        val rule = rules[consumer] ?: return emptySet()
        return if (edge in rule.edgeKinds) rule.allowedDependencies else emptySet()
    }

    override fun isAllowed(consumer: TargetType, dependency: TargetType, edge: EdgeKind): Boolean {
        return dependency in allowedTypes(consumer, edge)
    }

    override fun ruleFor(consumer: TargetType): RestrictionRule? = rules[consumer]

    /** Snapshot of current rules (useful for tests/debug). */
    fun allRules(): Map<TargetType, RestrictionRule> = rules.toMap()
}
