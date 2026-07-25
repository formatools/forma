package tools.forma.config

/**
 * Project-global named boolean feature flags (F-099 / GH #126).
 *
 * Declare once on root `androidProjectConfiguration(featureFlags = …)` /
 * [AndroidProjectSettings.featureFlags]. Read when applying dependencies and
 * (optionally) target features. **Not** per-module plugin shopping and **not**
 * AGP [FormaBuildFeatures] (different concern — BuildFeatures vs product flags).
 *
 * ## Defaults
 *
 * - Empty map is the default: every unknown name is **off** (`false`).
 * - [get] / [isEnabled] never throw for unknown names — missing = false.
 * - [require] throws if the name was never declared (strict call sites).
 *
 * ## One global way
 *
 * Flags live only on project configuration. Do not invent per-`impl` Boolean
 * parameters for each product toggle, free-form plugin id lists gated by flags,
 * or binary-only flag maps (too late for feature modules). See
 * `docs/TARGET-FEATURE-OPTIONS.md`.
 *
 * ```kotlin
 * androidProjectConfiguration(
 *     // ...
 *     featureFlags = FormaFeatureFlags(
 *         "daggerReflect" to true, // dev: reflection DI
 *     ),
 * )
 * ```
 */
data class FormaFeatureFlags(
    private val flags: Map<String, Boolean> = emptyMap(),
) {
    /**
     * Convenience constructor for call sites:
     * `FormaFeatureFlags("daggerReflect" to true, "offlineMode" to false)`.
     */
    constructor(vararg pairs: Pair<String, Boolean>) : this(mapOf(*pairs))

    /**
     * Whether [name] is enabled. Unknown names return **false**
     * (safe default — undeclared flag = off).
     */
    operator fun get(name: String): Boolean = flags[name] ?: false

    /** Same as [get] — unknown names are false. */
    fun isEnabled(name: String): Boolean = this[name]

    /**
     * Declared value for [name], or throws if the flag was never declared.
     *
     * Prefer [get] / [isEnabled] when unknown-as-false is the correct product
     * default. Use [require] only when a missing declaration is a configuration
     * error (e.g. a shared recipe that must be toggled explicitly).
     */
    fun require(name: String): Boolean =
        flags[name]
            ?: throw IllegalArgumentException(
                "Unknown feature flag '$name'. Declare it once via " +
                    "androidProjectConfiguration(featureFlags = FormaFeatureFlags(...)). " +
                    "See docs/TARGET-FEATURE-OPTIONS.md."
            )

    /** True when [name] appears in the declared map (even if the value is false). */
    fun contains(name: String): Boolean = name in flags

    /** Immutable snapshot of declared flags. */
    fun toMap(): Map<String, Boolean> = flags.toMap()

    companion object {
        /** Empty flags — every [get] / [isEnabled] returns false. */
        val EMPTY: FormaFeatureFlags = FormaFeatureFlags()
    }
}
