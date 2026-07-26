androidApp(
    packageName = "tools.forma.examples.android.ndk.root",
    dependencies = deps(
        target(":root-res"),
        target(":common:hello:android-util"),
    )
)
