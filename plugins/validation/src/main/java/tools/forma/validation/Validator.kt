package tools.forma.validation

import tools.forma.validation.error.ProjectValidationError
import tools.forma.target.FormaTarget
import org.gradle.api.Project

interface Validator {
    fun validate(project: Project)
}

object EmptyValidator : Validator {
    override fun validate(project: Project) = Unit
}

fun FormaTarget.validate(name: String): Boolean {
    return name == suffix || name.endsWith("-$suffix")
}

fun Project.validate(target: FormaTarget) {
    validator(target).validate(this)
}

fun validator(vararg targets: FormaTarget): Validator = object : Validator {
    override fun validate(project: Project) {
        validateName(project.name, *targets)
    }
}

fun validateName(
    name: String,
    vararg targets: FormaTarget
) {
    //Name should match with at least one targetName
    if (targets.map { it.validate(name) }.contains(true).not()) {
        throwProjectValidationError(name, targets.toList())
    }
}

fun throwProjectValidationError(
    name: String,
    targets: List<FormaTarget>
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
    vararg allowedTargets: FormaTarget
) {
    throw ProjectValidationError(
        """
            Project ${project.name}: incorrect type of dependencies
            Allowed only ${allowedTargets.map { it.suffix }} types
        """.trimIndent()
    )
}