import org.gradle.api.JavaVersion

/**
 * Limitations:
 * No DataBindings Support
 * No BuildConfig Support
 */
// TODO use jacoco by default
// TODO disable transitive deps by default
// TODO publishing
// TODO owners
// TODO modules dependency restrictions
// TODO api for module dependencies
// TODO should we mark dependencies?
// TODO how we make sure dependency sources is correct?
// TODO Should we use extension class as the last arg for complex config?
// TODO Should we restrict build flavor per module?
// TODO Manifest placeholders
// TODO Configuration override
// TODO 3rd party plugins
// TODO 3rd party annotation processors
data class Configuration(
    val minSdk: Int,
    val targetSdk: Int,
    val compileSdk: Int,
    val kotlinVersion: String,
    val agpVersion: String,
    val versionCode: Int,
    val versionName: String,
    val javaVersionCompatibility: JavaVersion = JavaVersion.VERSION_1_8 // Java/Kotlin configuration
)