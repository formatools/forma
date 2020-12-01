package tools.forma.validation

import org.gradle.api.Project
import org.gradle.tooling.BuildException
import java.io.File
import java.lang.IllegalStateException

fun Project.validateDirectoryContent(
    errorMsg: String,
    dir: String = "./src/main",
    validator: (Array<File>) -> Boolean
) {
    val files = file(dir)
        .listFiles() ?: throw buildException(project.name, "'$dir' does not exists")
    if (!validator(files)) {
        throw buildException(
            project.name,
            """$errorMsg
                  |Current list of files in $dir:
                  |${files.joinToString("\n") { it.name }}
                """.trimMargin()
        )
    }
}

fun buildException(projectName: String, errorMsg: String) = BuildException(
    "Error configuring '$projectName'",
    IllegalStateException(errorMsg)
)