// Navigation graphs only — resources target (flat structure, not androidLibrary).
// Uses Path B derived type (F-072) so safe-args is owned by the type, not call-site chain.
navigationRes(
    packageName = "tools.forma.sample.core.navigation.library",
    dependencies = deps(
        androidx.navigation
    )
)
