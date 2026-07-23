import org.gradle.api.JavaVersion

buildscript {
    androidProjectConfiguration(
        project = rootProject,
        minSdk = 23,
        targetSdk = 37,
        compileSdk = 37,
        agpVersion = "9.3.0",
        // Metro 1.x runtime is JVM 11 bytecode
        javaVersionCompatibility = JavaVersion.VERSION_11,
        // extraPlugins = classpath only (buildscript). Type-owned plugins auto-apply per target.
        extraPlugins = listOf(
            libs.plugins.toolsFormaExamplesDefs,
            libs.plugins.devZacsweersMetro,
        ),
    )
}
