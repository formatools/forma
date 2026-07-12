package tools.forma.validation

import tools.forma.validation.error.ProjectValidationError
import tools.forma.target.FormaTarget
import tools.forma.target.TargetTemplate
import org.gradle.api.Project

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
 * Validators are **identity-cached** for a given set of [TargetTemplate] instances so
 * multi-module configuration does not allocate a fresh anonymous [Validator] (and
 * intermediate lists) on every `impl` / `api` / … call (F-017 / GH #106).
 */
fun validator(vararg targets: TargetTemplate): Validator {
    if (targets.isEmpty()) return EmptyValidator
    if (targets.size == 1) {
        val only = targets[0]
        return singleValidators.getOrPut(only) { SingleSuffixValidator(only) }
    }
    // Identity-based key: TargetTemplate objects are singletons in Forma.
    val key = targets.toList()
    return multiValidators.getOrPut(key) { MultiSuffixValidator(targets.copyOf()) }
}

private val singleValidators = java.util.concurrent.ConcurrentHashMap<TargetTemplate, Validator>()
private val multiValidators = java.util.concurrent.ConcurrentHashMap<List<TargetTemplate>, Validator>()

private class SingleSuffixValidator(
    private val template: TargetTemplate
) : Validator {
    private val suffix: String = template.suffix
    private val dashSuffix: String = "-$suffix"

    override fun validate(target: FormaTarget) {
        val name = target.name
        if (name == suffix || name.endsWith(dashSuffix)) return
        throwProjectValidationError(name, listOf(template))
    }
}

private class MultiSuffixValidator(
    private val templates: Array<out TargetTemplate>
) : Validator {
    private val suffixes: Array<String> = Array(templates.size) { templates[it].suffix }
    private val dashSuffixes: Array<String> = Array(suffixes.size) { "-${suffixes[it]}" }

    override fun validate(target: FormaTarget) {
        val name = target.name
        for (i in suffixes.indices) {
            if (name == suffixes[i] || name.endsWith(dashSuffixes[i])) return
        }
        throwProjectValidationError(name, templates.asList())
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
