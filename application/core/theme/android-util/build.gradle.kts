androidUtil(
    packageName = "tools.forma.sample.core.theme.android.util",
    owner = Teams.core,
    dependencies = deps(
        androidx.appcompat,
        google.material,
        google.dagger
    ) + deps(
        target(":core:theme:res")
    )
)
