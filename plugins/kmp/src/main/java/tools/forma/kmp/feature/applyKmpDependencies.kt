package tools.forma.kmp.feature

import forEach
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import tools.forma.config.FormaSettingsStore
import tools.forma.deps.core.CompileOnly
import tools.forma.deps.core.ConfigurationType
import tools.forma.deps.core.EmptyDependency
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.Implementation
import tools.forma.deps.core.RuntimeOnly
import tools.forma.deps.core.TargetSpec
import tools.forma.deps.core.resolveFeatureFlags
import tools.forma.validation.Validator

/**
 * Apply Forma dependencies onto KMP **commonMain** / **commonTest** via the Kotlin
 * source-set dependency API (design §6.3 option 2).
 *
 * Project-dep suffix validation matches [tools.forma.deps.core.applyDependencies]
 * (including F-090 exclusions). Does **not** use bare `implementation` configuration
 * names that break MPP.
 *
 * - [dependencies] → commonMain
 * - [testDependencies] → commonTest
 *
 * Platform-specific attrs (`androidDependencies` / `jvmDependencies`) deferred — see
 * KDoc on [tools.forma.kmp.kmpLibrary] (open decision #2).
 */
fun Project.applyKmpDependencies(
    validator: Validator,
    dependencies: FormaDependency = EmptyDependency,
    testDependencies: FormaDependency = EmptyDependency,
) {
    if (dependencies === EmptyDependency && testDependencies === EmptyDependency) {
        return
    }

    val featureFlags = FormaSettingsStore.featureFlagsOrEmpty()
    val resolvedDependencies = dependencies.resolveFeatureFlags(featureFlags)
    val resolvedTestDependencies = testDependencies.resolveFeatureFlags(featureFlags)

    if (
        resolvedDependencies === EmptyDependency &&
            resolvedTestDependencies === EmptyDependency
    ) {
        return
    }

    val kotlinExt =
        extensions.findByType(KotlinMultiplatformExtension::class.java)
            ?: error(
                "applyKmpDependencies requires org.jetbrains.kotlin.multiplatform " +
                    "(call applyKotlinMultiplatform first)"
            )

    val appliedPlugins = mutableSetOf<String>()
    val hasPluginDeps = FormaSettingsStore.dependencyPlugins.isNotEmpty()

    fun validateProjectDep(spec: TargetSpec) {
        val depProject = spec.target.project
        if (
            !FormaSettingsStore.isExcludedFromDependencyValidation(
                projectName = depProject.name,
                projectPath = depProject.path,
            )
        ) {
            validator.validate(spec.target)
        }
    }

    fun KotlinDependencyHandler.addConfig(config: ConfigurationType, notation: Any) {
        when (config) {
            is Implementation -> implementation(notation)
            is CompileOnly -> compileOnly(notation)
            is RuntimeOnly -> runtimeOnly(notation)
            else -> implementation(notation)
        }
    }

    fun wireSourceSet(
        sourceSetName: String,
        dep: FormaDependency,
        validateProjects: Boolean,
    ) {
        if (dep === EmptyDependency) return
        kotlinExt.sourceSets.named(sourceSetName).configure {
            dependencies {
                dep.forEach(
                    nameAction = { spec ->
                        val plugin =
                            if (hasPluginDeps) FormaSettingsStore.pluginFor(spec.name) else null
                        if (plugin != null) {
                            val pluginName = plugin.plugin.get().pluginId.split(":")[0]
                            if (appliedPlugins.add(pluginName)) {
                                apply(plugin = pluginName)
                            }
                            implementation(spec.name)
                        } else {
                            addConfig(spec.config, spec.name)
                        }
                    },
                    targetAction = { spec ->
                        if (validateProjects) {
                            validateProjectDep(spec)
                        }
                        addConfig(spec.config, spec.target.project)
                    },
                    fileAction = { spec ->
                        addConfig(spec.config, files(spec.file))
                    },
                    platformAction = { spec ->
                        // KGP 2.3 deprecates KotlinDependencyHandler.platform — use Gradle's BOM helper.
                        implementation(project.dependencies.platform(spec.name))
                    },
                )
            }
        }
    }

    wireSourceSet(
        sourceSetName = KotlinSourceSet.COMMON_MAIN_SOURCE_SET_NAME,
        dep = resolvedDependencies,
        validateProjects = true,
    )
    wireSourceSet(
        sourceSetName = KotlinSourceSet.COMMON_TEST_SOURCE_SET_NAME,
        dep = resolvedTestDependencies,
        validateProjects = false,
    )
}
