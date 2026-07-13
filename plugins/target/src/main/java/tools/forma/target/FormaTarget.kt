package tools.forma.target

import org.gradle.api.Project
import tools.forma.core.target.TargetRef

/**
 * Gradle adapter for a target. Implements core TargetRef (name sufficient for validation).
 * TargetTemplate remains the legacy suffix-based template for DSL compat (F-022).
 */
class FormaTarget(val project: Project) : TargetRef {
    override val name: String = project.name
}
