// Path A Firebase — plugins + BOM/SDK owned by type; call site attributes only.
firebaseBinary(
    packageName = "tools.forma.examples.android.firebase",
    versionCode = 1,
    versionName = "0.1.0",
    dependencies = deps(
        target(":root-app"),
    ),
)
