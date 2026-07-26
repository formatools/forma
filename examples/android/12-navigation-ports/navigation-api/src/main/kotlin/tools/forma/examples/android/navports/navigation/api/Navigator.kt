package tools.forma.examples.android.navports.navigation.api

/**
 * Navigation port used by feature UI. Implementations live at the composition root
 * (see `root-app` JetpackNavigator) — never inside feature `impl`.
 */
interface Navigator {
    fun navigate(to: AppDestination)
    fun back()
}

/** Host Activity (or app shell) exposes the port to fragments without DI scaffolding. */
interface NavigatorProvider {
    val navigator: Navigator
}
