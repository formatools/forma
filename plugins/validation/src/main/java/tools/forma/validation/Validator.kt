package tools.forma.validation

import tools.forma.validation.error.ProjectValidationError
import tools.forma.target.FormaTarget
import tools.forma.target.TargetTemplate
import org.gradle.api.Project
import tools.forma.core.target.targetType
import tools.forma.core.validation.FormaValidationException
import tools.forma.core.validation.TargetValidator as CoreValidator
import tools.forma.core.validation.dependencyTypeValidator as coreDependencyTypeValidator
import java.util.concurrent.ConcurrentHashMap

interface Validator {
    fun validate(target: FormaTarget)
}

object EmptyValidator : Validator {
    override fun validate(target: FormaTarget) = Unit
}

fun TargetTemplate.validate(name: String): Boolean {
    return name == suffix || name.endsWith("-$suffix", ignoreCase = false)
}

fun FormaTarget.validate(target: TargetTemplate) {
    validator(target).validate(this)
}

/**
 * Name-suffix validator for project dependency / self-type checks.
 *
 * Validators are **identity-cached** (F-017) for a given set of [TargetTemplate] instances.
 * Thin facade over core `dependencyTypeValidator` (which also uses identity caching on TargetType).
 * Legacy call sites and exception types (ProjectValidationError) are preserved exactly.
 */
fun validator(vararg targets: TargetTemplate): Validator {
    if (targets.isEmpty()) return EmptyValidator
    if (targets.size == 1) {
        val only = targets[0]
        return singleValidators.getOrPut(only) { LegacySingleValidator(only) }
    }
    // Identity-based key using the original TargetTemplate objects (singletons).
    val key = targets.toList()
    return multiValidators.getOrPut(key) { LegacyMultiValidator(targets.copyOf()) }
}

private val singleValidators = ConcurrentHashMap<TargetTemplate, Validator>()
private val multiValidators = ConcurrentHashMap<List<TargetTemplate>, Validator>()

/** Stable mapping from legacy template -> synthetic TargetType for core delegation + cache hits. */
private val legacyTypeCache = ConcurrentHashMap<TargetTemplate, tools.forma.core.target.TargetType>()
private fun legacyTypeFor(t: TargetTemplate): tools.forma.core.target.TargetType =
    legacyTypeCache.getOrPut(t) { targetType("legacy.${t.suffix}", t.suffix) }

private class LegacySingleValidator(
    private val template: TargetTemplate
) : Validator {
    private val coreType = legacyTypeFor(template)
    private val coreV: CoreValidator = coreDependencyTypeValidator(listOf(coreType))

    override fun validate(target: FormaTarget) {
        try {
            coreV.validate(target) // FormaTarget implements TargetRef
        } catch (ex: FormaValidationException) {
            // Preserve exact legacy exception type + message shape for all consumers
            throwProjectValidationError(target.name, listOf(template))
        }
    }
}

private class LegacyMultiValidator(
    private val templates: Array<out TargetTemplate>
) : Validator {
    private val coreTypes = templates.map { legacyTypeFor(it) }
    private val coreV: CoreValidator = coreDependencyTypeValidator(coreTypes)

    override fun validate(target: FormaTarget) {
        try {
            coreV.validate(target)
        } catch (ex: FormaValidationException) {
            throwProjectValidationError(target.name, templates.asList())
        }
    }
}

fun validateName(
    name: String,
    vararg targets: TargetTemplate
) {
    for (template in targets) {
        if (template.validate(name)) return
    }
    throwProjectValidationError(name, targets.toList())
}

fun throwProjectValidationError(
    name: String,
    targets: List<TargetTemplate>
) {
    throw ProjectValidationError(
        """
            Project $name: name does not match allowed target type(s)
            Allowed name suffix(es): ${targets.joinToString { it.suffix }}
            (Used for self-type checks and project-dependency type checks.)
        """.trimIndent()
    )
}

fun throwProjectDepsValidationError(
    project: Project,
    vararg allowedTargets: TargetTemplate
) {
    throw ProjectValidationError(
        """
            Project ${project.name}: incorrect type of dependencies
            Allowed only ${allowedTargets.map { it.suffix }} types
        """.trimIndent()
    )
}

/**
 * Bridge from core [TargetValidator] (obtained via registry) to the legacy [Validator]
 * interface consumed by [applyDependencies] and feature selfValidator slots.
 *
 * Wraps [FormaValidationException] as [ProjectValidationError] so existing callers,
 * tests, and error shape expectations are unchanged.
 */
fun CoreValidator.asValidator(): Validator = object : Validator {
    override fun validate(target: FormaTarget) {
        try {
            this@asValidator.validate(target)
        } catch (ex: FormaValidationException) {
            throw ProjectValidationError(ex.message ?: "Project ${target.name}: validation failed")
        }
    }
}
