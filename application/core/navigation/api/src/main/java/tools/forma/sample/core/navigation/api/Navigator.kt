package tools.forma.sample.core.navigation.api

/**
 * Navigation port used by feature UI. Implementations live at the composition root
 * (see root-app [tools.forma.sample.root.library.navigation.JetpackNavigator]) —
 * never inside feature `impl`.
 */
interface Navigator {
    fun navigate(to: AppDestination)
    fun back()
}

/**
 * Application (or activity) exposes the port so feature fragments can resolve it
 * without depending on androidx.navigation. Sample uses Application + [requireProvider]
 * pattern already established for other feature providers.
 */
interface NavigatorProvider {
    fun getNavigator(): Navigator
}
