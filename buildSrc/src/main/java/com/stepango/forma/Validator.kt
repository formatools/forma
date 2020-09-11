package com.stepango.forma

import org.gradle.api.Project

interface Validator {
    fun TargetName.validate(project: Project) = project.name.endsWith(suffix)
    fun validate(project: Project)
}

fun emptyValidator(): Validator = object : Validator {
    override fun validate(project: Project) { }
}

fun throwProjectValidationError(
    project: Project,
    targetName: TargetName
) {
    throw ProjectValidationError(
        """
            Project ${project.name}: name does not match type requirements
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