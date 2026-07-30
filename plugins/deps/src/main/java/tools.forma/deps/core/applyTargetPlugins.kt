package tools.forma.deps.core

import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import tools.forma.core.target.TargetType
import tools.forma.validation.EmptyValidator

/**
 * Auto-apply any TargetPluginSpec(s) bound to this TargetType.
 * Call after applyFeatures(...) and before/around applyDependencies.
 *
 * Plugins are looked up from the global TargetPluginRegistry (populated via
 * registerTargetPlugin or deriveTargetType). This is the mechanism that makes
 * "type owns plugin, call site has zero plugin surface".
 *
 * [configurationFeatures] must mirror the host DSL's processor map (e.g. KSP)
 * so companion deps that use [Ksp] / custom configurations can create those
 * configurations before [applyDependencies] adds them (F-115 Hilt Path A).
 */
fun Project.applyTargetPlugins(
    type: TargetType,
    configurationFeatures: Map<ConfigurationType, () -> Unit> = emptyMap(),
) {
    val specs = TargetPluginRegistry.get(type)
    specs.forEach { spec ->
        apply(plugin = spec.id)
        val deps = spec.dependencies
        // Mirror applyDependencies guard to avoid unnecessary {} block for empty.
        if (deps !== EmptyDependency && deps != EmptyDependency) {
            applyDependencies(
                validator = EmptyValidator,
                dependencies = deps,
                repositoriesConfiguration = EmptyRepositoriesConfiguration,
                configurationFeatures = configurationFeatures,
            )
        }
    }
}
