package tools.forma.deps.core

import tools.forma.config.FormaFeatureFlags

/**
 * Pure resolution of feature-flag-gated dependencies (F-099 / F-104 / GH #126 / #43).
 *
 * Specs carry optional flag metadata from `depsIf` / `depsUnless` /
 * [NamedDependency.whenFlag] / [TargetDependency.whenFlag] /
 * [featureImplementation]. Resolution runs against project-global
 * [FormaFeatureFlags] at **apply** time (see [applyDependencies]), not when the
 * helper is constructed.
 *
 * Unknown flag names are **false** ([FormaFeatureFlags.get]) — matching
 * `depsIf("missing", …)` drops the dep and `depsUnless("missing", …)` keeps it.
 *
 * ## What is gated
 *
 * - [NameSpec] (named / external coords) — F-099
 * - [TargetSpec] (first-party project targets) — F-104
 * - Files and platforms are unchanged (no flag metadata on those kinds yet)
 */

/** Canonical [FormaFeatureFlags] name for impl ↔ stub-impl swap (F-104 / GH #43). */
const val USE_FEATURE_STUBS_FLAG: String = "useFeatureStubs"

/** Gradle property that backs [USE_FEATURE_STUBS_FLAG] in examples (`-Pforma.useFeatureStubs=true`). */
const val USE_FEATURE_STUBS_PROPERTY: String = "forma.useFeatureStubs"

/** Whether this named dep should be applied given [flags]. Unconditional specs always include. */
fun NameSpec.isIncludedBy(flags: FormaFeatureFlags): Boolean =
    isFlagConditionMet(featureFlag, featureFlagExpected, flags)

/** Whether this target dep should be applied given [flags]. Unconditional specs always include. */
fun TargetSpec.isIncludedBy(flags: FormaFeatureFlags): Boolean =
    isFlagConditionMet(featureFlag, featureFlagExpected, flags)

private fun isFlagConditionMet(
    featureFlag: String?,
    featureFlagExpected: Boolean,
    flags: FormaFeatureFlags,
): Boolean {
    val flag = featureFlag ?: return true
    return flags.isEnabled(flag) == featureFlagExpected
}

/** Keep only name specs whose flag condition matches [flags]. */
fun List<NameSpec>.resolveNameFeatureFlags(flags: FormaFeatureFlags): List<NameSpec> =
    filter { it.isIncludedBy(flags) }

/** Keep only target specs whose flag condition matches [flags]. */
fun List<TargetSpec>.resolveTargetFeatureFlags(flags: FormaFeatureFlags): List<TargetSpec> =
    filter { it.isIncludedBy(flags) }

/**
 * Drop specs that fail their feature-flag condition.
 * Named deps and project targets are filtered; files and platforms pass through.
 */
fun FormaDependency.resolveFeatureFlags(flags: FormaFeatureFlags): FormaDependency =
    when (this) {
        EmptyDependency -> this
        is NamedDependency -> {
            val resolved = names.resolveNameFeatureFlags(flags)
            if (resolved.isEmpty()) EmptyDependency else NamedDependency(resolved)
        }
        is TargetDependency -> {
            val resolved = targets.resolveTargetFeatureFlags(flags)
            if (resolved.isEmpty()) EmptyDependency else TargetDependency(resolved)
        }
        is MixedDependency -> {
            val resolvedNames = names.resolveNameFeatureFlags(flags)
            val resolvedTargets = targets.resolveTargetFeatureFlags(flags)
            if (
                resolvedNames.isEmpty() &&
                    resolvedTargets.isEmpty() &&
                    files.isEmpty() &&
                    platforms.isEmpty()
            ) {
                EmptyDependency
            } else {
                MixedDependency(
                    names = resolvedNames,
                    targets = resolvedTargets,
                    files = files,
                    platforms = platforms,
                )
            }
        }
        is FileDependency -> this
        is PlatformDependency -> this
    }

/**
 * Pure impl ↔ stub-impl pair for composition roots (F-104).
 *
 * Builds two gated [TargetSpec] lists:
 * - [impl] included when [flag] is **off** / unknown (production default)
 * - [stub] included when [flag] is **on** (IDE / local stub mode)
 *
 * Preferred pattern is a **simple swap** (implementation of one side only). Both
 * sides keep their [TargetSpec.config] (default [Implementation]).
 *
 * Call sites should prefer the DSL [featureImplementation] in `dependencies.kt`.
 */
fun featureImplementationPair(
    impl: List<TargetSpec>,
    stub: List<TargetSpec>,
    flag: String = USE_FEATURE_STUBS_FLAG,
): List<TargetSpec> {
    require(impl.isNotEmpty()) { "featureImplementation requires at least one impl TargetSpec" }
    require(stub.isNotEmpty()) { "featureImplementation requires at least one stub TargetSpec" }
    val implGated =
        impl.map { spec ->
            TargetSpec(
                target = spec.target,
                config = spec.config,
                featureFlag = flag,
                featureFlagExpected = false,
            )
        }
    val stubGated =
        stub.map { spec ->
            TargetSpec(
                target = spec.target,
                config = spec.config,
                featureFlag = flag,
                featureFlagExpected = true,
            )
        }
    return implGated + stubGated
}
