androidApp(
    packageName = "tools.forma.examples.android.firebase.root",
    dependencies = deps(
        // Firebase SDKs are pulled onto the APK via type-owned plugins on firebaseBinary.
        // App code depends on Analytics/Crashlytics for compile (BOM versions from platform on binary).
        // For library modules, declare the same GAVs without versions only when a platform is
        // on this module — here we pin BOM-aligned coordinates for the library classpath.
        transitiveDeps(
            "com.google.firebase:firebase-crashlytics:19.4.4",
            "com.google.firebase:firebase-analytics:22.4.0",
        ),
        "androidx.appcompat:appcompat:1.7.1".dep,
    ) + deps(
        target(":root-res"),
    ),
)
