androidBinary(
    packageName = "tools.forma.examples.android.ndk",
    versionCode = 1,
    versionName = "0.1.0",
    dependencies = deps(
        target(":root-app"),
        target(":common:hello:android-util"),
        // Composition root may also depend on native directly for packaging.
        target(":common:hello:native"),
    )
)
