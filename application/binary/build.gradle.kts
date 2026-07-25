import tools.forma.android.utils.BuildConfiguration
import tools.forma.android.utils.FormaSigningConfig

androidBinary(
    packageName = "tools.forma.sample.app",
    owner = Teams.core,
    versionCode = 1,
    versionName = "0.0.1",
    // Compose enabled so binary can host Compose content from compose-widget deps.
    compose = true,
    // F-097 / GH #51: first-class signing on the APK root only.
    // demo-release.keystore is a committed *dummy* store (password "android") for docs/CI —
    // never commit production keystores or secrets. Real apps: read path/passwords from
    // env or project properties with empty defaults so assembleDebug stays green.
    signingConfigs = mapOf(
        "demoRelease" to FormaSigningConfig(
            storeFile = file("demo-release.keystore"),
            storePassword = "android",
            keyAlias = "androiddebugkey",
            keyPassword = "android",
        ),
        // Production sketch (not active — empty passwords would fail release signing):
        // "release" to FormaSigningConfig(
        //     storeFile = file(
        //         findProperty("forma.storeFile") as String?
        //             ?: System.getenv("FORMA_STORE_FILE")
        //             ?: "missing-release.keystore"
        //     ),
        //     storePassword = System.getenv("FORMA_STORE_PASSWORD") ?: "",
        //     keyAlias = System.getenv("FORMA_KEY_ALIAS") ?: "upload",
        //     keyPassword = System.getenv("FORMA_KEY_PASSWORD") ?: "",
        // ),
    ),
    // Wire release → dummy store so assembleRelease can succeed without secrets.
    // debug keeps AGP's built-in debug signing (assembleDebug needs no custom config).
    buildTypeSigning = mapOf(
        "release" to "demoRelease",
    ),
    buildConfiguration = BuildConfiguration(
        buildTypes = mapOf(
            "release" to {
                isMinifyEnabled = false
            },
        ),
    ),
    dependencies = deps(
        target(":root-app"),

        target(":feature:home:api"),
        target(":feature:home:impl"),
        target(":feature:characters:core:api"),
        target(":feature:characters:core:impl"),
        target(":feature:characters:list:api"),
        target(":feature:characters:list:impl"),
        target(":feature:characters:detail:api"),
        target(":feature:characters:detail:impl"),
        target(":feature:characters:favorite:api"),
        target(":feature:characters:favorite:impl"),

        target(":common:extensions:android-util"),
//        target(":common:util-native"),
        target(":common:greeting:compose-widget"),
        target(":core:mvvm:ui-library"),
        target(":core:di:android-util")
    )
)
// TODO: enable when create crashlytics project
// (would be a derived firebaseBinary type per Path B, not .withPlugins chain)
