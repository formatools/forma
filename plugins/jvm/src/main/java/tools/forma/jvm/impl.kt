package tools.forma.jvm

import org.gradle.api.Project
import tools.forma.deps.core.EmptyDependency
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.applyDependencies
import tools.forma.jvm.feature.applyKotlinJvm
import tools.forma.jvm.target.JvmTargetRegistry
import tools.forma.jvm.target.JvmTargetTypes
import tools.forma.jvm.target.registerJvmDefaults
import tools.forma.target.FormaTarget
import tools.forma.validation.asValidator

/**
 * Feature **implementation** (pure JVM).
 *
 * Dagger2-friendly boundaries:
 * - May depend on `api`, `library`, `util`, `test-util`.
 * - **Must not** depend on other `impl` modules.
 *   Composition of impls happens at a binary root (`binary(...)`, F-031).
 */
fun Project.impl(
    packageName: String,
    dependencies: FormaDependency = EmptyDependency,
    testDependencies: FormaDependency = EmptyDependency
) {
    registerJvmDefaults()

    JvmTargetRegistry.selfValidator(JvmTargetTypes.impl).asValidator()
        .validate(FormaTarget(this))

    applyKotlinJvm()

    applyDependencies(
        validator = JvmTargetRegistry.validatorFor(JvmTargetTypes.impl).asValidator(),
        dependencies = dependencies,
        testDependencies = testDependencies
    )
}
