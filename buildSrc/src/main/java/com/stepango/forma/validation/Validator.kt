package com.stepango.forma.validation

import com.stepango.forma.error.ProjectValidationError
import com.stepango.forma.module.LibraryModule
import com.stepango.forma.module.ModuleDefinition
import org.gradle.api.Project

interface Validator {
    fun validate(project: Project)
}

object EmptyValidator : Validator {
    override fun validate(project: Project) = Unit
}

fun ModuleDefinition.validate(name: String): Boolean {
    return name == suffix || name.endsWith("-$suffix")
}

fun Project.validate(definition: ModuleDefinition) {
    validator(definition).validate(this)
}

fun validator(vararg moduleDefinitions: ModuleDefinition): Validator = object : Validator {
    override fun validate(project: Project) {
        validateName(project.name, *moduleDefinitions)
    }
}

fun validateName(
    name: String,
    vararg moduleDefinitions: ModuleDefinition
) {
    //Name should match with at least one targetName
    if (moduleDefinitions.map { it.validate(name) }.contains(true).not()) {
        throwProjectValidationError(name, LibraryModule)
    }
}

fun throwProjectValidationError(
    name: String,
    moduleDefinition: ModuleDefinition
) {
    throw ProjectValidationError(
        """
            Project ${name}: name does not match type requirements
            Projects of type "${moduleDefinition::class.simpleName}" should contain name suffix "${moduleDefinition.suffix}" 
        """.trimIndent()
    )
}

fun throwProjectDepsValidationError(
    project: Project,
    vararg allowedTargets: ModuleDefinition
) {
    throw ProjectValidationError(
        """
            Project ${project.name}: incorrect type of dependencies
            Allowed only ${allowedTargets.map { it.suffix }} types
        """.trimIndent()
    )
}