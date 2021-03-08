package tools.forma.android.config

import com.android.build.gradle.internal.dsl.BuildType

sealed class BuildConfiguration(
    val typeName: String,
    val typeCustomizer: BuildType.() -> Unit = {}
)

class None : BuildConfiguration("none")

class Debug : BuildConfiguration("debug")

class Release(val debuggable: Boolean = false,
              val proguardFiles: List<String>) : BuildConfiguration("release")

class Custom(name: String, customizer: BuildType.() -> Unit) : BuildConfiguration(name, customizer)
