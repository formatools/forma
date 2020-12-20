package tools.forma.deps

import org.gradle.api.Project

/**
 * Dependency wrapper
 * TODO: inline class
 */

sealed class ConfigurationType(val name: String)
object Implementation : ConfigurationType("implementation")
object CompileOnly : ConfigurationType("compileOnly")
object RuntimeOnly : ConfigurationType("runtimeOnly")
object AnnotationProcessor : ConfigurationType("annotationProcessor")
object Kapt : ConfigurationType("kapt")
class Custom(name: String) : ConfigurationType(name)

sealed class DepSpec(val config: ConfigurationType)

class ProjectSpec(val project: Project, config: ConfigurationType) : DepSpec(config)

class NameSpec(
    val name: String,
    config: ConfigurationType,
    val transitive: Boolean = false
) : DepSpec(config)

typealias DepType = List<DepSpec>