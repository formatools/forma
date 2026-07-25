package tools.forma.deps.core

import tools.forma.config.FormaFeatureFlags

/**
 * Pure resolution of feature-flag-gated named dependencies (F-099 / GH #126).
 *
 * Specs carry optional [NameSpec.featureFlag] metadata from `depsIf` /
 * `depsUnless` / [NamedDependency.whenFlag]. Resolution runs against
 * project-global [FormaFeatureFlags] at **apply** time (see
 * [applyDependencies]), not when the helper is constructed.
 *
 * Unknown flag names are **false** ([FormaFeatureFlags.get]) — matching
 * `depsIf("missing", …)` drops the dep and `depsUnless("missing", …)` keeps it.
 */

/** Whether this named dep should be applied given [flags]. Unconditional specs always include. */
fun NameSpec.isIncludedBy(flags: FormaFeatureFlags): Boolean {
    val flag = featureFlag ?: return true
    return flags.isEnabled(flag) == featureFlagExpected
}

/** Keep only name specs whose flag condition matches [flags]. */
fun List<NameSpec>.resolveFeatureFlags(flags: FormaFeatureFlags): List<NameSpec> =
    filter { it.isIncludedBy(flags) }

/**
 * Drop named deps that fail their feature-flag condition.
 * Targets, files, and platforms are unchanged (no flag metadata on those kinds yet).
 */
fun FormaDependency.resolveFeatureFlags(flags: FormaFeatureFlags): FormaDependency =
    when (this) {
        EmptyDependency -> this
        is NamedDependency -> {
            val resolved = names.resolveFeatureFlags(flags)
            if (resolved.isEmpty()) EmptyDependency else NamedDependency(resolved)
        }
        is MixedDependency ->
            MixedDependency(
                names = names.resolveFeatureFlags(flags),
                targets = targets,
                files = files,
            )
        is TargetDependency -> this
        is FileDependency -> this
        is PlatformDependency -> this
    }
