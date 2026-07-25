package tools.forma.kmp

import org.gradle.api.Project
import tools.forma.core.target.TargetType
import tools.forma.deps.core.EmptyDependency
import tools.forma.deps.core.FormaDependency
import tools.forma.deps.core.applyTargetPlugins
import tools.forma.deps.fleet.registerFormaLayout
import tools.forma.kmp.feature.applyKmpDependencies
import tools.forma.kmp.feature.applyKotlinMultiplatform
import tools.forma.kmp.settings.KmpSettingsStore
import tools.forma.kmp.target.KmpTargetRegistry
import tools.forma.kmp.target.KmpTargetTypes
import tools.forma.kmp.target.registerKmpDefaults
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.target.FormaTarget
import tools.forma.validation.asValidator

/**
 * Shared multiplatform **library** (default workhorse). Type id `kmp.library`,
 * name suffix `kmp-library`.
 *
 * Applies `org.jetbrains.kotlin.multiplatform` and project-global platforms from
 * [kmpProjectConfiguration] (default jvm + android). Dependencies go to **commonMain**;
 * [testDependencies] to **commonTest**.
 *
 * ## Layout / packageName
 * [packageName] is recorded via [registerFormaLayout] with
 * `requirePackageSourceDir = false` so Android/JVM `src/main` layout checks are not
 * forced onto KMP modules. Prefer `src/commonMain/kotlin/…` (see docs/KMP-TARGETS.md §4.4).
 * Full commonMain fleet check/generate lands in **F-109**.
 *
 * ## Platform-specific deps (open decision #2)
 * Optional `androidDependencies` / `jvmDependencies` attrs are **deferred** — ship only when
 * a consumer needs them. Until then, put shared deps in [dependencies]; platform-only code
 * can use expect/actual with deps declared in a follow-up.
 *
 * @return [Unit] — no builder chains (F-081)
 */
fun Project.kmpLibrary(
    packageName: String,
    dependencies: FormaDependency = EmptyDependency,
    testDependencies: FormaDependency = EmptyDependency,
    owner: Owner = NoOwner,
): Unit =
    kmpTarget(
        type = KmpTargetTypes.library,
        packageName = packageName,
        dependencies = dependencies,
        testDependencies = testDependencies,
        owner = owner,
    )

/**
 * Shared public contracts (expect/actual-friendly). Type id `kmp.api`, suffix `kmp-api`.
 */
fun Project.kmpApi(
    packageName: String,
    dependencies: FormaDependency = EmptyDependency,
    testDependencies: FormaDependency = EmptyDependency,
    owner: Owner = NoOwner,
): Unit =
    kmpTarget(
        type = KmpTargetTypes.api,
        packageName = packageName,
        dependencies = dependencies,
        testDependencies = testDependencies,
        owner = owner,
    )

/**
 * Shared utilities / extensions. Type id `kmp.util`, suffix `kmp-util`.
 */
fun Project.kmpUtil(
    packageName: String,
    dependencies: FormaDependency = EmptyDependency,
    testDependencies: FormaDependency = EmptyDependency,
    owner: Owner = NoOwner,
): Unit =
    kmpTarget(
        type = KmpTargetTypes.util,
        packageName = packageName,
        dependencies = dependencies,
        testDependencies = testDependencies,
        owner = owner,
    )

/**
 * Shared test helpers (commonTest + platform tests). Type id `kmp.test-util`,
 * suffix `kmp-test-util`.
 */
fun Project.kmpTestUtil(
    packageName: String,
    dependencies: FormaDependency = EmptyDependency,
    testDependencies: FormaDependency = EmptyDependency,
    owner: Owner = NoOwner,
): Unit =
    kmpTarget(
        type = KmpTargetTypes.testUtil,
        packageName = packageName,
        dependencies = dependencies,
        testDependencies = testDependencies,
        owner = owner,
    )

/**
 * Common flow for all KMP target DSLs (attributes only, Unit return).
 *
 * 1. [registerKmpDefaults]
 * 2. self-validator via [KmpTargetRegistry]
 * 3. layout metadata (commonMain-oriented; see F-109)
 * 4. [applyKotlinMultiplatform]
 * 5. [applyTargetPlugins]
 * 6. [applyKmpDependencies] → commonMain / commonTest
 */
private fun Project.kmpTarget(
    type: TargetType,
    packageName: String,
    dependencies: FormaDependency,
    testDependencies: FormaDependency,
    owner: Owner,
) {
    @Suppress("UNUSED_VARIABLE")
    val unusedOwner = owner // reserved for F-owners parity; mandatoryOwners path is Android-only today

    registerKmpDefaults()
    // Ensure settings exist even when root skipped kmpProjectConfiguration (defaults).
    if (!KmpSettingsStore.isSettingsStored) {
        KmpSettingsStore.store(KmpSettingsStore.settingsOrDefaults())
    }

    KmpTargetRegistry.selfValidator(type).asValidator().validate(FormaTarget(this))

    // Do not force src/main/kotlin — KMP uses commonMain. requirePackageSourceDir=false
    // keeps formaLayoutCheck from failing greenfield KMP modules (F-109 extends fleet).
    registerFormaLayout(packageName, requirePackageSourceDir = false)

    applyKotlinMultiplatform(packageName = packageName)

    applyTargetPlugins(type)

    applyKmpDependencies(
        validator = KmpTargetRegistry.validatorFor(type).asValidator(),
        dependencies = dependencies,
        testDependencies = testDependencies,
    )
}
