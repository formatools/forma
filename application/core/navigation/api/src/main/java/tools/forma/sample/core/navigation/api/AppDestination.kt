package tools.forma.sample.core.navigation.api

/**
 * Presentation-layer destinations. Feature `impl` emits these; only the composition-root
 * adapter maps them onto Jetpack Navigation / Safe Args.
 */
sealed interface AppDestination {
    data class CharacterDetail(val characterId: Long) : AppDestination
}

/**
 * Bundle argument keys for destinations. Must match `android:name` on nav-graph
 * `<argument>` entries. Features read plain Bundle values; they never import Safe Args `*Args`.
 */
object CharacterDetailArgs {
    const val CHARACTER_ID = "character_id"
}
