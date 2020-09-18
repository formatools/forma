package com.stepango.forma

import org.gradle.api.Project

interface Validator {
    fun validate(project: Project)
}

object EmptyValidator : Validator {
    override fun validate(project: Project) = Unit
}

fun TargetName.validate(name: String) = name.endsWith(suffix)

fun validator(vararg targetNames: TargetName): Validator = object : Validator {
    override fun validate(project: Project) {
        validateName(project.name, *targetNames)
    }
}

fun validateName(
    name: String,
    vararg targetNames: TargetName
) {
    //Name should match with at least one targetName
    if (targetNames.map { it.validate(name) }.contains(true).not()) {
        throwProjectValidationError(name, Library)
    }
}

fun throwProjectValidationError(
    name: String,
    targetName: TargetName
) {
    throw ProjectValidationError(
        """
            Project ${name}: name does not match type requirements
            Projects of type "${targetName::class.simpleName}" should contain name suffix "${targetName.suffix}" 
        """.trimIndent()
    )
}

fun throwProjectDepsValidationError(
    project: Project,
    vararg allowedTargets: TargetName
) {
    throw ProjectValidationError(
        """
            Project ${project.name}: incorrect type of dependencies
            Allowed only ${allowedTargets.map { it.suffix }} types
        """.trimIndent()
    )
}