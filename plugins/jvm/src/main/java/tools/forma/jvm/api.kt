package tools.forma.jvm

import org.gradle.api.Project
import tools.forma.deps.core.EmptyDependency
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.applyDependencies
import tools.forma.deps.fleet.registerFormaLayout
import tools.forma.jvm.feature.applyKotlinJvm
import tools.forma.jvm.target.JvmTargetRegistry
import tools.forma.jvm.target.JvmTargetTypes
import tools.forma.jvm.target.registerJvmDefaults
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.target.FormaTarget
import tools.forma.validation.asValidator

/**
 * Feature **API** surface (pure JVM).
 *
 * Dagger2-friendly public contracts only.
 * May depend on other `api` and pure `jvm.library` modules.
 * Must not depend on `impl`, `util` (except through library contracts if modeled that way).
 */
fun Project.api(
    packageName: String,
    owner: Owner = NoOwner,
    dependencies: FormaDependency = EmptyDependency
) {
    registerJvmDefaults()

    JvmTargetRegistry.selfValidator(JvmTargetTypes.api).asValidator()
        .validate(FormaTarget(this))
    registerFormaLayout(packageName)

    applyKotlinJvm()

    applyDependencies(
        validator = JvmTargetRegistry.validatorFor(JvmTargetTypes.api).asValidator(),
        dependencies = dependencies
    )
}
