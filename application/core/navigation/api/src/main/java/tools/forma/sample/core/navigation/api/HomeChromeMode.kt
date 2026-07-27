package tools.forma.sample.core.navigation.api

/**
 * Logical home-shell chrome mode. Pushed from the composition-root adapter when the
 * active destination changes — feature ViewModels never see NavController or graph R.ids.
 */
enum class HomeChromeMode {
    /** Bottom nav + app bar visible (tab roots). */
    NavigationScreen,

    /** Full-screen content (e.g. character detail). */
    FullScreen,
}
