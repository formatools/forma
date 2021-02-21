package tools.forma.android.config

import com.android.build.gradle.internal.dsl.BuildType

sealed class BuildConfiguration(
    val typeName: String = "",
    val typeCustomizer: BuildType.() -> Unit = {}
    // TODO Insert singing configuration here
)

class None : BuildConfiguration("none")
class Debug : BuildConfiguration("debug")
class Release(val debuggable: Boolean = false) : BuildConfiguration("release")
class Custom(typeName: String, typeCustomizer: BuildType.() -> Unit) : BuildConfiguration(typeName, typeCustomizer)
