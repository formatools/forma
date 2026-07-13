package tools.forma.android.validation

import org.gradle.api.Project
import java.io.File
import tools.forma.core.validation.NoResourcesUnderMain
import tools.forma.core.validation.OnlyLayoutResources
import tools.forma.core.validation.OnlyResourcesUnderMain
import tools.forma.validation.buildException
// validateDirectoryContent kept in :validation as Gradle-coupled facade (core ContentRule is pure)

/**
 * Gradle-coupled content helpers. Pure predicates live in core (F-022); listing + error wrapping
 * uses the validation facade so behavior and messages for existing DSLs are unchanged.
 */

fun Project.disallowResources() {
    val dir = "./src/main"
    val files = file(dir).listFiles() ?: throw buildException(project.name, "'$dir' does not exists")
    val names = files.filter(File::isDirectory).map { it.name }
    val failure = NoResourcesUnderMain.check(names)
    if (failure != null) {
        throw buildException(
            project.name,
            """$failure
                  |Current list of files in $dir:
                  |${files.joinToString("\n") { it.name }}
                """.trimMargin()
        )
    }
}

fun Project.onlyAllowResources() {
    val dir = "./src/main"
    val files = file(dir).listFiles() ?: throw buildException(project.name, "'$dir' does not exists")
    val names = files.filter(File::isDirectory).map { it.name }
    val failure = OnlyResourcesUnderMain.check(names)
    if (failure != null) {
        throw buildException(
            project.name,
            """$failure
                  |Current list of files in $dir:
                  |${files.joinToString("\n") { it.name }}
                """.trimMargin()
        )
    }
}

fun Project.onlyAllowLayouts() {
    val dir = "./src/main/res"
    val files = file(dir).listFiles() ?: throw buildException(project.name, "'$dir' does not exists")
    val names = files.filter(File::isDirectory).map { it.name }
    val failure = OnlyLayoutResources.check(names)
    if (failure != null) {
        throw buildException(
            project.name,
            """$failure
                  |Current list of files in $dir:
                  |${files.joinToString("\n") { it.name }}
                """.trimMargin()
        )
    }
}

// Note: validateDirectoryContent remains in tools.forma.validation for any external Gradle-coupled usage.
