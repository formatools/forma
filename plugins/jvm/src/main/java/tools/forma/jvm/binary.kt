package tools.forma.jvm

import org.gradle.api.Project
import org.gradle.api.plugins.JavaApplication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import tools.forma.deps.core.EmptyDependency
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.applyDependencies
import tools.forma.jvm.feature.applyKotlinJvm
import tools.forma.jvm.target.JvmTargetRegistry
import tools.forma.jvm.target.JvmTargetTypes
import tools.forma.jvm.target.registerJvmDefaults
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.target.FormaTarget
import tools.forma.validation.asValidator

/**
 * Pure JVM **binary** (composition root + runnable application).
 *
 * - Wires multiple `impl` modules (and api/library/util) at a single root.
 * - Applies `org.jetbrains.kotlin.jvm` + `application` (enables `./gradlew :binary:run`).
 * - `mainClass` must be the Kotlin `...MainKt` for a top-level `fun main()` in `Main.kt`.
 *
 * Matrix: binary may depend on api, impl, library, util, test-util.
 */
fun Project.binary(
    packageName: String,
    mainClass: String,
    owner: Owner = NoOwner,
    dependencies: FormaDependency = EmptyDependency
) {
    registerJvmDefaults()

    JvmTargetRegistry.selfValidator(JvmTargetTypes.binary).asValidator()
        .validate(FormaTarget(this))

    applyKotlinJvm()
    apply(plugin = "application")

    configure<JavaApplication> {
        this.mainClass.set(mainClass)
    }

    applyDependencies(
        validator = JvmTargetRegistry.validatorFor(JvmTargetTypes.binary).asValidator(),
        dependencies = dependencies
    )
}
