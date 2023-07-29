package tools.forma.deps.core

import java.io.File
import tools.forma.target.FormaTarget

sealed interface ConfigurationType {
    val name: String
}

object Implementation : ConfigurationType {
    override val name: String = "implementation"
}

object CompileOnly : ConfigurationType {
    override val name: String = "compileOnly"
}

object RuntimeOnly : ConfigurationType {
    override val name: String = "runtimeOnly"
}

object AnnotationProcessor : ConfigurationType {
    override val name: String = "annotationProcessor"
}

object Kapt : ConfigurationType {
    override val name: String = "kapt"
}

@JvmInline value class CustomConfiguration(override val name: String) : ConfigurationType

sealed class DepSpec(val config: ConfigurationType)

class TargetSpec(val target: FormaTarget, config: ConfigurationType) : DepSpec(config)

class FileSpec(val file: File, config: ConfigurationType) : DepSpec(config)

class NameSpec(val name: String, config: ConfigurationType, val transitive: Boolean = false) :
    DepSpec(config)

class PlatformSpec(val name: String, config: ConfigurationType, val transitive: Boolean = false) :
    DepSpec(config)

typealias DepType = List<DepSpec>
