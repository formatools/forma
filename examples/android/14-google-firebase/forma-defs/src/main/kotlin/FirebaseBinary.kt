import org.gradle.api.Project
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.android.target.registerTargetPlugin
import tools.forma.android.target.targetPlugin
import tools.forma.android.utils.BuildConfiguration
import tools.forma.android.utils.FormaSigningConfig
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.deps.core.FormaDependency
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner

/**
 * F-114 — Google Firebase (Crashlytics) via type-owned plugins on the APK root.
 *
 * Path A on [AndroidTargetTypes.binary]: every [firebaseBinary] / [androidBinary]
 * in this example project gets GMS + Crashlytics. Thin [firebaseBinary] DSL forces
 * registration to load and documents intent (same pattern as metroApp).
 *
 * Call sites stay attributes-only — no plugin ids.
 */
// transitiveDeps: Crashlytics/Analytics need firebase-common on the consumer compile
// classpath when Application code lives on binary. deps()/String.dep are non-transitive.
private val firebaseBomAndSdks: FormaDependency =
    transitivePlatform("com.google.firebase:firebase-bom:33.16.0") +
        transitiveDeps(
            "com.google.firebase:firebase-crashlytics",
            "com.google.firebase:firebase-analytics",
        )

private val googleServices = targetPlugin(id = "com.google.gms.google-services")
private val crashlytics = targetPlugin(
    id = "com.google.firebase.crashlytics",
    dependencies = firebaseBomAndSdks,
)

/** Side-effecting load: bind GMS + Crashlytics to binary once. */
internal object FirebasePluginBindings {
    init {
        registerTargetPlugin(AndroidTargetTypes.binary, googleServices, crashlytics)
    }
}

/**
 * APK composition root with Firebase Crashlytics + Analytics on the classpath
 * and type-owned Gradle plugins applied. Dummy `google-services.json` lives on
 * the binary module (CI-safe placeholder — not a live Firebase project).
 */
fun Project.firebaseBinary(
    packageName: String,
    versionCode: Int,
    versionName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency(),
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    signingConfigs: Map<String, FormaSigningConfig> = emptyMap(),
    buildTypeSigning: Map<String, String> = emptyMap(),
) {
    FirebasePluginBindings
    androidBinary(
        packageName = packageName,
        owner = owner,
        versionCode = versionCode,
        versionName = versionName,
        dependencies = dependencies,
        buildConfiguration = buildConfiguration,
        signingConfigs = signingConfigs,
        buildTypeSigning = buildTypeSigning,
    )
}
