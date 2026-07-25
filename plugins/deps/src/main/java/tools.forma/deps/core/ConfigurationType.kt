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

/** Kotlin Symbol Processing configuration (`ksp`). Sole annotation-processing path (F-093). */
object Ksp : ConfigurationType {
    override val name: String = "ksp"
}

@JvmInline value class CustomConfiguration(override val name: String) : ConfigurationType

sealed class DepSpec(val config: ConfigurationType)

class TargetSpec(val target: FormaTarget, config: ConfigurationType = Implementation) : DepSpec(config)

class FileSpec(val file: File, config: ConfigurationType) : DepSpec(config)

/**
 * External module coordinate (GAV or catalog-resolved name).
 *
 * Optional [featureFlag] gates inclusion at apply time against project-global
 * [tools.forma.config.FormaFeatureFlags] (F-099). When null, the dep is always applied.
 * See `depsIf` / `depsUnless` / [NamedDependency.whenFlag] and
 * [resolveFeatureFlags].
 */
class NameSpec(
    val name: String,
    config: ConfigurationType,
    val transitive: Boolean = false,
    /** When non-null, include only if project flags match [featureFlagExpected]. */
    val featureFlag: String? = null,
    /** Expected [tools.forma.config.FormaFeatureFlags] value for [featureFlag] (default true). */
    val featureFlagExpected: Boolean = true,
) : DepSpec(config)

class PlatformSpec(val name: String, config: ConfigurationType, val transitive: Boolean = false) :
    DepSpec(config)

typealias DepType = List<DepSpec>
