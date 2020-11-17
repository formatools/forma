package tools.forma.android.validation

import tools.forma.android.error.ProjectValidationError
import tools.forma.android.target.TargetDefinition
import org.gradle.api.Project

interface Validator {
    fun validate(project: Project)
}

object EmptyValidator : Validator {
    override fun validate(project: Project) = Unit
}

fun TargetDefinition.validate(name: String): Boolean {
    return name == suffix || name.endsWith("-$suffix")
}

fun Project.validate(definition: TargetDefinition) {
    validator(definition).validate(this)
}

fun validator(vararg targetDefinitions: TargetDefinition): Validator = object : Validator {
    override fun validate(project: Project) {
        validateName(project.name, *targetDefinitions)
    }
}

fun validateName(
    name: String,
    vararg targetDefinitions: TargetDefinition
) {
    //Name should match with at least one targetName
    if (targetDefinitions.map { it.validate(name) }.contains(true).not()) {
        throwProjectValidationError(name, targetDefinitions.toList())
    }
}

fun throwProjectValidationError(
    name: String,
    targetDefinition: List<TargetDefinition>
) {
    throw ProjectValidationError(
        """
            Project ${name}: name does not match type requirements
            Projects of type "${targetDefinition.joinToString { it::class.simpleName ?: "" }} should contain name suffix from the list: "${targetDefinition.joinToString { it.suffix }}" 
        """.trimIndent()
    )
}

fun throwProjectDepsValidationError(
    project: Project,
    vararg allowedTargets: TargetDefinition
) {
    throw ProjectValidationError(
        """
            Project ${project.name}: incorrect type of dependencies
            Allowed only ${allowedTargets.map { it.suffix }} types
        """.trimIndent()
    )
}