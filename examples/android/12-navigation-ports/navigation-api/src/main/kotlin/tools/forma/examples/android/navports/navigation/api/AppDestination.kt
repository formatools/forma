package tools.forma.examples.android.navports.navigation.api

/**
 * Presentation-layer destinations. Feature `impl` emits these; only the composition-root
 * adapter maps them onto Jetpack Navigation / Safe Args.
 */
sealed interface AppDestination {
    data class ItemDetail(val itemId: Long) : AppDestination
}
