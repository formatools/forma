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
 * Pure JVM **library** (shared code, no Android).
 *
 * May only depend on `util` + `test-util` (parity with prior JVM library row).
 */
fun Project.library(
    packageName: String,
    dependencies: FormaDependency = EmptyDependency,
    owner: Owner = NoOwner,
    testDependencies: FormaDependency = EmptyDependency
) {
    registerJvmDefaults()

    JvmTargetRegistry.selfValidator(JvmTargetTypes.library).asValidator()
        .validate(FormaTarget(this))
    registerFormaLayout(packageName)

    applyKotlinJvm()

    applyDependencies(
        validator = JvmTargetRegistry.validatorFor(JvmTargetTypes.library).asValidator(),
        dependencies = dependencies,
        testDependencies = testDependencies
    )
}
