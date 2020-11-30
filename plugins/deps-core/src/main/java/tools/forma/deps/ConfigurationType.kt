package tools.forma.deps

import org.funktionale.either.Either
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

data class ProjectSpec(val project: Project, val config: ConfigurationType)

data class NameSpec(
    val name: String,
    val config: ConfigurationType,
    val transitive: Boolean = false
)

typealias DepType = List<Either<NameSpec, ProjectSpec>>