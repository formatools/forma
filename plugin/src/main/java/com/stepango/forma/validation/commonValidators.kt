package com.stepango.forma.validation

import org.gradle.api.Project
import java.io.File

fun Project.disallowResources() = validateDirectoryContent(
    dir = "./src/main",
    errorMsg = "Please make sure this does not contain `res` directory"
) { files ->
    files.filter(File::isDirectory)
        .map { it.name }
        .run { !contains("res") }
}