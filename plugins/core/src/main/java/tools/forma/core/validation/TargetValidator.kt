package tools.forma.core.validation

import tools.forma.core.target.NameMatcher
import tools.forma.core.target.SuffixNameMatcher
import tools.forma.core.target.TargetRef
import tools.forma.core.target.TargetType
import java.util.concurrent.ConcurrentHashMap

/**
 * SPI for validating a target (by name against allowed types or other rules).
 * Core is pure; Gradle/AGP concerns stay in platform plugins.
 */
fun interface TargetValidator {
    fun validate(target: TargetRef)
}

/** No-op validator that accepts any target. Useful for composition roots during transition. */
object AcceptAny : TargetValidator {
    override fun validate(target: TargetRef) = Unit
}

/**
 * Factory for a validator that accepts dependency (or self) names matching ANY of the allowed TargetTypes.
 *
 * Identity-cached (F-017): calling with the same TargetType instance(s) returns the exact same
 * validator instance (===). Keys are by TargetType reference for singles and by content-equal
 * list for multis (preserves cache behavior of legacy ConcurrentHashMap on templates).
 */
fun dependencyTypeValidator(
    allowed: Collection<TargetType>,
    nameMatcher: NameMatcher = SuffixNameMatcher,
): TargetValidator {
    if (allowed.isEmpty()) return AcceptAny
    if (allowed.size == 1) {
        val only = allowed.first()
        return singleValidators.getOrPut(only) { SingleTypeValidator(only, nameMatcher) }
    }
    val key = allowed.toList()
    return multiValidators.getOrPut(key) { MultiTypeValidator(key.toTypedArray(), nameMatcher) }
}

/**
 * Factory for self-type validation: the target's own name must match the expected type's suffix rule.
 * Reuses the single-type cache for efficiency.
 */
fun selfTypeValidator(
    expected: TargetType,
    nameMatcher: NameMatcher = SuffixNameMatcher,
): TargetValidator = dependencyTypeValidator(listOf(expected), nameMatcher)

/** Exception thrown by core validators on mismatch. Message shape kept close to legacy for recognizability. */
class FormaValidationException(message: String) : RuntimeException(message)

// ---- internal cached impls ----

private val singleValidators = ConcurrentHashMap<TargetType, TargetValidator>()
private val multiValidators = ConcurrentHashMap<List<TargetType>, TargetValidator>()

private class SingleTypeValidator(
    private val type: TargetType,
    private val matcher: NameMatcher
) : TargetValidator {
    override fun validate(target: TargetRef) {
        if (matcher.matches(target.name, type)) return
        throw FormaValidationException(
            """
            Project ${target.name}: name does not match allowed target type(s)
            Allowed name suffix(es): ${type.nameSuffix}
            (Used for self-type checks and project-dependency type checks.)
            """.trimIndent()
        )
    }
}

private class MultiTypeValidator(
    private val types: Array<TargetType>,
    private val matcher: NameMatcher
) : TargetValidator {
    override fun validate(target: TargetRef) {
        val name = target.name
        for (t in types) {
            if (matcher.matches(name, t)) return
        }
        val allowed = types.joinToString { it.nameSuffix }
        throw FormaValidationException(
            """
            Project $name: name does not match allowed target type(s)
            Allowed name suffix(es): $allowed
            (Used for self-type checks and project-dependency type checks.)
            """.trimIndent()
        )
    }
}
