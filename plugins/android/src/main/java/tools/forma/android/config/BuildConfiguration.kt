package tools.forma.android.config

import com.android.build.gradle.internal.dsl.BuildType

sealed class BuildConfiguration(
    val typeName: String,
    val buildFields: BuildFields = emptyBuildFields(),
    val typeCustomizer: BuildType.() -> Unit = {}
)

class None
    : BuildConfiguration("none")

class Debug(
    buildFields: BuildFields = emptyBuildFields()
) : BuildConfiguration("debug", buildFields)

class Release(
    val debuggable: Boolean = false,
    val proguardFiles: List<String>,
    buildFields: BuildFields = emptyBuildFields()
) : BuildConfiguration("release", buildFields)

class Custom(
    name: String,
    customizer: BuildType.() -> Unit
) : BuildConfiguration(name, typeCustomizer = customizer)
