package tools.forma.android.utils

import com.android.build.api.dsl.ApkSigningConfig
import com.android.build.api.dsl.ApplicationVariantDimension
import java.io.File
import org.gradle.api.NamedDomainObjectContainer

/**
 * Typed APK signing identity for `androidBinary` (F-097 / GH #51).
 *
 * Maps cleanly onto AGP [ApkSigningConfig]. **Binary-only:** release/APK signing is
 * configured on the composition root, not on library targets (`impl`, `uiLibrary`,
 * `androidApp` shell). Do not teach raw `android { signingConfigs { … } }` as the
 * happy path.
 *
 * @param storeFile keystore file (use `project.file("…")` at the call site)
 * @param storePassword keystore password (prefer env / project properties in real apps;
 *   never commit production secrets)
 * @param keyAlias key alias inside the store
 * @param keyPassword key password
 * @param storeType optional store type (e.g. `"pkcs12"`); null leaves AGP default
 * @param enableV1Signing optional APK Signature Scheme v1 flag
 * @param enableV2Signing optional APK Signature Scheme v2 flag
 * @param enableV3Signing optional APK Signature Scheme v3 flag
 * @param enableV4Signing optional APK Signature Scheme v4 flag
 */
data class FormaSigningConfig(
    val storeFile: File,
    val storePassword: String,
    val keyAlias: String,
    val keyPassword: String,
    val storeType: String? = null,
    val enableV1Signing: Boolean? = null,
    val enableV2Signing: Boolean? = null,
    val enableV3Signing: Boolean? = null,
    val enableV4Signing: Boolean? = null,
)

/**
 * Resolved field bag for unit tests and AGP apply.
 * Optional flags are omitted from the bag when null so AGP defaults stay untouched.
 */
data class AppliedSigningFields(
    val storeFile: File,
    val storePassword: String,
    val keyAlias: String,
    val keyPassword: String,
    val storeType: String? = null,
    val enableV1Signing: Boolean? = null,
    val enableV2Signing: Boolean? = null,
    val enableV3Signing: Boolean? = null,
    val enableV4Signing: Boolean? = null,
)

/** Pure projection of [FormaSigningConfig] (no AGP types). */
fun FormaSigningConfig.toAppliedFields(): AppliedSigningFields =
    AppliedSigningFields(
        storeFile = storeFile,
        storePassword = storePassword,
        keyAlias = keyAlias,
        keyPassword = keyPassword,
        storeType = storeType,
        enableV1Signing = enableV1Signing,
        enableV2Signing = enableV2Signing,
        enableV3Signing = enableV3Signing,
        enableV4Signing = enableV4Signing,
    )

/** Writes [AppliedSigningFields] onto AGP [ApkSigningConfig]. */
fun AppliedSigningFields.applyTo(target: ApkSigningConfig) {
    target.storeFile = storeFile
    target.storePassword = storePassword
    target.keyAlias = keyAlias
    target.keyPassword = keyPassword
    storeType?.let { target.storeType = it }
    enableV1Signing?.let { target.enableV1Signing = it }
    enableV2Signing?.let { target.enableV2Signing = it }
    enableV3Signing?.let { target.enableV3Signing = it }
    enableV4Signing?.let { target.enableV4Signing = it }
}

/** Convenience: [FormaSigningConfig] → AGP [ApkSigningConfig]. */
fun FormaSigningConfig.applyTo(target: ApkSigningConfig) {
    toAppliedFields().applyTo(target)
}

/**
 * Pure validation of build-type → signing-config name pairs.
 *
 * @return ordered pairs ready to assign
 * @throws IllegalArgumentException when a referenced name is missing
 */
fun resolveBuildTypeSigningPairs(
    buildTypeSigning: Map<String, String>,
    buildTypeNames: Set<String>,
    signingConfigNames: Set<String>,
): List<Pair<String, String>> {
    if (buildTypeSigning.isEmpty()) return emptyList()
    return buildTypeSigning.map { (buildTypeName, signingName) ->
        require(buildTypeName in buildTypeNames) {
            "buildTypeSigning references unknown build type '$buildTypeName' " +
                "(known: ${buildTypeNames.sorted()})"
        }
        require(signingName in signingConfigNames) {
            "buildTypeSigning maps '$buildTypeName' → unknown signing config '$signingName' " +
                "(known: ${signingConfigNames.sorted()})"
        }
        buildTypeName to signingName
    }
}

/**
 * Registers named signing configs on an AGP application [signingConfigs] container.
 *
 * Uses create-or-get semantics: creates when missing, otherwise updates in place
 * (AGP already ships a `debug` config).
 */
@Suppress("UNCHECKED_CAST")
internal fun NamedDomainObjectContainer<*>.applySigningConfigs(
    configs: Map<String, FormaSigningConfig>,
) {
    if (configs.isEmpty()) return
    val container = this as NamedDomainObjectContainer<ApkSigningConfig>
    configs.forEach { (name, forma) ->
        val signing = container.findByName(name) ?: container.create(name)
        forma.applyTo(signing)
    }
}

/**
 * Bridges build type name → signing config name after both containers exist.
 *
 * Preferred over `signingConfig = …` inside [BuildConfiguration] build-type lambdas:
 * those lambdas are typed as [com.android.build.api.dsl.BuildType], which does not
 * expose application [ApplicationVariantDimension.signingConfig].
 */
@Suppress("UNCHECKED_CAST")
internal fun applyBuildTypeSigning(
    buildTypes: NamedDomainObjectContainer<*>,
    signingConfigs: NamedDomainObjectContainer<*>,
    buildTypeSigning: Map<String, String>,
) {
    if (buildTypeSigning.isEmpty()) return
    val types = buildTypes as NamedDomainObjectContainer<ApplicationVariantDimension>
    val signings = signingConfigs as NamedDomainObjectContainer<ApkSigningConfig>
    val pairs = resolveBuildTypeSigningPairs(
        buildTypeSigning = buildTypeSigning,
        buildTypeNames = types.names,
        signingConfigNames = signings.names,
    )
    pairs.forEach { (buildTypeName, signingName) ->
        types.getByName(buildTypeName).signingConfig = signings.getByName(signingName)
    }
}
