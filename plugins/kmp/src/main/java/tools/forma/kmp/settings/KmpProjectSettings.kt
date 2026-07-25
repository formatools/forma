package tools.forma.kmp.settings

/**
 * Project-global KMP platform policy (F-107 / docs/KMP-TARGETS.md §4.3, §7.1).
 *
 * Platforms are chosen **once** for the product — never re-listed per module.
 * Defaults match the design happy path: **jvm + android** both on.
 */
data class KmpPlatforms(
    val jvm: Boolean = true,
    val android: Boolean = true,
) {
    init {
        require(jvm || android) {
            "KmpPlatforms: at least one of jvm/android must be true"
        }
    }
}

/**
 * Stored by [tools.forma.kmp.kmpProjectConfiguration] (or defaults on first DSL use).
 *
 * @property platforms fleet-wide Kotlin targets (not call-site attrs)
 * @property jvmTarget Kotlin/JVM bytecode target string (default `"11"`)
 * @property kotlinVersion Kotlin Gradle plugin version on the buildscript classpath
 * @property agpVersion AGP version for the Android KMP library plugin classpath when
 *   [KmpPlatforms.android] is true; null means "align with existing Android toolchain
 *   or fail at apply if android is requested without AGP"
 */
data class KmpProjectSettings(
    val platforms: KmpPlatforms = KmpPlatforms(),
    val jvmTarget: String = DEFAULT_KMP_JVM_TARGET,
    val kotlinVersion: String? = null,
    val agpVersion: String? = null,
)

/** Default JVM target for KMP jvm() — parity with pure JVM plugin (F-030). */
const val DEFAULT_KMP_JVM_TARGET: String = "11"

/**
 * Process-wide store for [KmpProjectSettings] (parallel spirit to FormaSettingsStore,
 * but owned by `:kmp` so the module never depends on `:android`).
 */
object KmpSettingsStore {
    @Volatile
    private var _settings: KmpProjectSettings? = null

    val isSettingsStored: Boolean
        get() = _settings != null

    val settings: KmpProjectSettings
        get() =
            _settings
                ?: error(
                    "KmpProjectSettings not configured. Call kmpProjectConfiguration(...) " +
                        "from the root buildscript, or use a KMP DSL entry (which registers defaults)."
                )

    fun store(configuration: KmpProjectSettings) {
        _settings = configuration
    }

    /** Settings if [store] was called; otherwise null (does not throw). */
    fun settingsOrNull(): KmpProjectSettings? = _settings

    /**
     * Settings if stored; otherwise [KmpProjectSettings] defaults
     * (jvm+android, jvmTarget 11). Used by first-DSL-use default path.
     */
    fun settingsOrDefaults(): KmpProjectSettings = _settings ?: KmpProjectSettings()

    /** Test-only reset. */
    internal fun clear() {
        _settings = null
    }
}
