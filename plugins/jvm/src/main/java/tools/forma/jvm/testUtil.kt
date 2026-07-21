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
 * JVM **test-util** helpers.
 *
 * Intended for test code shared across modules. Depends on `test-util` + `util` (+ library).
 */
fun Project.testUtil(
    packageName: String,
    owner: Owner = NoOwner,
    dependencies: FormaDependency = EmptyDependency
) {
    registerJvmDefaults()

    JvmTargetRegistry.selfValidator(JvmTargetTypes.testUtil).asValidator()
        .validate(FormaTarget(this))
    registerFormaLayout(packageName)

    applyKotlinJvm()

    applyDependencies(
        validator = JvmTargetRegistry.validatorFor(JvmTargetTypes.testUtil).asValidator(),
        dependencies = dependencies
    )
}
