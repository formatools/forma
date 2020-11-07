package com.stepango.forma.validation

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
        .listFiles()!!
    if (!validator(files)) {
        throw IllegalStateException(
            """$errorMsg
                  |Current list of files in $dir:
                  |${files.joinToString("\n") { it.name }}
                """.trimMargin()
        ).let {
            BuildException("Error configuring ${project.name}", it)
        }
    }
}