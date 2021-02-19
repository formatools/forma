package tools.forma.android.validation

import org.gradle.api.Project
import java.io.File
import tools.forma.validation.validateDirectoryContent

fun Project.disallowResources() = validateDirectoryContent(
    dir = "./src/main",
    errorMsg = "Please make sure this does not contain `res` directory"
) { files ->
    files.filter(File::isDirectory)
        .map { it.name }
        .run { !contains("res") }
}

fun Project.onlyAllowResources() = validateDirectoryContent(
    dir = "./src/main",
    errorMsg = "Please make sure this target only contains `res` folder in `src/main`"
) {
    it.filter(File::isDirectory)
        .run { size == 1 && first().name == "res" }
}

fun Project.onlyAllowLayouts() = validateDirectoryContent(
    dir = "./src/main/res",
    errorMsg = "Please make sure this target only contains `layout.*` folders in `src/main/res`"
) {
    it.filter(File::isDirectory)
        .all { it.name.startsWith("layout") }
}
