package tools.forma.core.target

import tools.forma.core.restriction.MutableRestrictionGraph
import tools.forma.core.restriction.RestrictionGraph
import tools.forma.core.validation.ContentRule
import tools.forma.core.validation.TargetValidator
import tools.forma.core.validation.dependencyTypeValidator
import tools.forma.core.validation.selfTypeValidator

/**
 * Registration for a target type with its dependency allow-list, optional content rules,
 * and name matching strategy.
 */
data class TargetRegistration(
    val type: TargetType,
    val allowedDependencies: Set<TargetType>,
    val contentRules: List<ContentRule> = emptyList(),
    val nameMatcher: NameMatcher = SuffixNameMatcher,
    val metadata: Map<String, String> = emptyMap(),
)

/**
 * Registry of target types and their restriction rules.
 * Platform plugins (Android first) register their types + matrix once at configuration time.
 * Consumers (DSL entrypoints) obtain validators from here instead of hardcoding allow-lists.
 */
interface TargetRegistry {
    /** Register or replace a type's full definition (last write wins for a given id). */
    fun register(registration: TargetRegistration)

    /** Lookup by stable id (e.g. "android.impl", "jvm.library"). */
    fun get(id: String): TargetType?

    /** All registered types whose nameSuffix matches (may return >1 on suffix collision per §7). */
    fun getBySuffix(suffix: String): Collection<TargetType>

    /** Snapshot of all registrations. */
    fun all(): Collection<TargetRegistration>

    /** Current restriction graph view built from registrations. */
    fun restrictionGraph(): RestrictionGraph

    /** Validator for project dependencies of the given consumer type. Identity-cached. */
    fun validatorFor(consumer: TargetType): TargetValidator

    /** Validator for the consumer's own project name (self-type check). Identity-cached. */
    fun selfValidator(type: TargetType): TargetValidator
}

/**
 * Default in-memory implementation.
 * - Stores by type.id
 * - Builds restriction rules from allowedDependencies on register (replace semantics)
 * - validatorFor / selfValidator delegate to core factories (which provide identity caching)
 * - getBySuffix supports shared suffixes (library vs jvm.library)
 */
class DefaultTargetRegistry(
    private val defaultNameMatcher: NameMatcher = SuffixNameMatcher,
) : TargetRegistry {

    private val byId = mutableMapOf<String, TargetRegistration>()
    // We compute graph on demand from authoritative registrations to guarantee exact sets (no merge surprises on re-register).
    private val validatorCache = mutableMapOf<TargetType, TargetValidator>()
    private val selfValidatorCache = mutableMapOf<TargetType, TargetValidator>()

    override fun register(registration: TargetRegistration) {
        byId[registration.type.id] = registration
        // Invalidate caches that could be affected (consumer or any that listed it); simplest: clear all.
        validatorCache.clear()
        selfValidatorCache.clear()
    }

    override fun get(id: String): TargetType? = byId[id]?.type

    override fun getBySuffix(suffix: String): Collection<TargetType> =
        byId.values
            .filter { it.type.nameSuffix == suffix }
            .map { it.type }

    override fun all(): Collection<TargetRegistration> = byId.values.toList()

    override fun restrictionGraph(): RestrictionGraph {
        val g = MutableRestrictionGraph()
        byId.values.forEach { reg ->
            if (reg.allowedDependencies.isNotEmpty()) {
                g.allow(reg.type, reg.allowedDependencies)
            }
        }
        return g
    }

    override fun validatorFor(consumer: TargetType): TargetValidator {
        return validatorCache.getOrPut(consumer) {
            val reg = byId[consumer.id]
            val allowed = reg?.allowedDependencies ?: emptySet()
            val matcher = reg?.nameMatcher ?: defaultNameMatcher
            dependencyTypeValidator(allowed, matcher)
        }
    }

    override fun selfValidator(type: TargetType): TargetValidator {
        return selfValidatorCache.getOrPut(type) {
            val reg = byId[type.id]
            val matcher = reg?.nameMatcher ?: defaultNameMatcher
            selfTypeValidator(type, matcher)
        }
    }
}
