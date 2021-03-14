package tools.forma.android.utils

import com.android.build.gradle.internal.CompileOptions
import com.android.build.gradle.internal.dsl.BuildType
import com.android.build.gradle.internal.dsl.DefaultConfig
import tools.forma.android.config.FormaConfiguration
import tools.forma.android.config.BuildConfiguration
import tools.forma.android.config.BuildFields
import tools.forma.android.config.Debug
import tools.forma.android.config.Release
import tools.forma.android.config.Custom
import tools.forma.android.config.None
import org.gradle.api.NamedDomainObjectContainer

internal fun DefaultConfig.applyFrom(
    formaConfiguration: FormaConfiguration,
    testInstrumentationRunnerClass: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any>
) {
    minSdkVersion(formaConfiguration.minSdk)
    targetSdkVersion(formaConfiguration.targetSdk)
    versionCode = formaConfiguration.versionCode
    versionName = formaConfiguration.versionName

    testInstrumentationRunner = testInstrumentationRunnerClass
    consumerProguardFiles(*consumerMinificationFiles.toTypedArray())
    manifestPlaceholders(manifestPlaceholders)

    vectorDrawables.useSupportLibrary = formaConfiguration.vectorDrawablesUseSupportLibrary
}

internal fun CompileOptions.applyFrom(config: FormaConfiguration) {
    sourceCompatibility = config.javaVersionCompatibility
    targetCompatibility = config.javaVersionCompatibility
}

internal fun NamedDomainObjectContainer<BuildType>.applyFrom(config: BuildConfiguration) {
    when (config) {
        is Debug -> getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            isTestCoverageEnabled = true
            applyFrom(config.buildFields)
            config.typeCustomizer(this)
        }

        is Release -> getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = config.debuggable
            isTestCoverageEnabled = false

            check(config.proguardFiles.isNotEmpty()) {
                "BuildType ${config.typeName}: empty rules files. " +
                "Use default files `proguard-android-optimize.txt` or `proguard-android.txt`"
            }
            for (file in config.proguardFiles) {
                proguardFile(file)
            }

            applyFrom(config.buildFields)
            config.typeCustomizer(this)
        }

        is Custom -> getByName(config.typeName) {
            config.typeCustomizer(this)
        }

        is None -> return
    }
}

private fun BuildType.applyFrom(fields: BuildFields) {
    fields.values.forEach { (name, value) ->
        when (value) {
            is String -> buildConfigField("String", name, value)
            is Int -> buildConfigField("int", name, value.toString())
            is Boolean -> buildConfigField("boolean", name, value.toString())
            else -> throw IllegalArgumentException("Unsupported build config field: name=$name, type=$value")
        }
    }
}
