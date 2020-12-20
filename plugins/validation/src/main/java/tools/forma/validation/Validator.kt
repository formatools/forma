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
    return name == suffix || name.endsWith("-$suffix")
}

fun FormaTarget.validate(target: TargetTemplate) {
    validator(target).validate(this)
}

fun validator(vararg targets: TargetTemplate): Validator = object : Validator {
    override fun validate(target: FormaTarget) {
        validateName(target.name, *targets)
    }
}

fun validateName(
    name: String,
    vararg targets: TargetTemplate
) {
    //Name should match with at least one targetName
    if (targets.map { it.validate(name) }.contains(true).not()) {
        throwProjectValidationError(name, targets.toList())
    }
}

fun throwProjectValidationError(
    name: String,
    targets: List<TargetTemplate>
) {
    throw ProjectValidationError(
        """
            Project ${name}: name does not match type requirements
            Projects of type "${targets.joinToString { it::class.simpleName ?: "" }} should contain name suffix from the list: "${targets.joinToString { it.suffix }}" 
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