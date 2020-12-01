package tools.forma.deps

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import java.io.File
import java.io.FileInputStream
import java.util.Properties

fun Project.findAndroidJar(sdk: Int): ConfigurableFileCollection =
    project.files("${findSdkLocation(project)}/platforms/android-$sdk/android.jar")

fun findSdkLocation(project: Project): File {
    val rootDir = project.rootDir
    val localProperties = File(rootDir, "local.properties")
    if (localProperties.exists()) {
        val properties = Properties()
        FileInputStream(localProperties).use { instr ->
            properties.load(instr)
        }
        var sdkDirProp = properties.getProperty("sdk.dir")
        return if (sdkDirProp != null) {
            File(sdkDirProp)
        } else {
            sdkDirProp = properties.getProperty("android.dir")
            if (sdkDirProp != null) {
                File(rootDir, sdkDirProp)
            } else {
                throw RuntimeException("No sdk.dir property defined in local.properties file.")
            }
        }
    } else {
        val envVar = System.getenv("ANDROID_HOME")
        if (envVar != null) {
            return File(envVar)
        } else {
            val property = System.getProperty("android.home")
            if (property != null) {
                return File(property)
            }
        }
    }
    throw RuntimeException("Can't find SDK path")
}