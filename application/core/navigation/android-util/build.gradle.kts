// Nav-aware host helpers consumed by composition-root adapter / home shell only.
// Feature happy path uses core/navigation/api ports — not this module for destinations.
androidUtil(
    packageName = "tools.forma.sample.core.navigation.android.util",
    owner = Teams.core,
    dependencies = deps(
        androidx.core_ktx,
        androidx.fragment,
        androidx.navigation,
        androidx.appcompat,
        androidx.viewmodel,
        google.material,
    ) + deps(
        target(":core:navigation:res"),
    ),
)
