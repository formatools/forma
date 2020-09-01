package com.stepango.forma

import org.gradle.api.Project

interface Validator {
    fun TargetName.validate(project: Project) = project.name.endsWith(suffix)
    fun validate(project: Project)
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