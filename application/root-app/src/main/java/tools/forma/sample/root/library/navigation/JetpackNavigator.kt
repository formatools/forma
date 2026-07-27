package tools.forma.sample.root.library.navigation

import androidx.navigation.NavController
import tools.forma.sample.core.navigation.api.AppDestination
import tools.forma.sample.core.navigation.api.Navigator
import tools.forma.sample.feature.characters.list.impl.ui.CharactersListFragmentDirections

/**
 * Sole composition-root adapter that may import [NavController] + Safe Args *Directions.
 * Feature `impl` never sees these types — Directions are generated into the fragment
 * package name from XML, but compiled only via `core/navigation/res`.
 *
 * Multi-backstack: [attach] is called whenever the selected bottom-nav tab's
 * NavController changes so [navigate]/[back] target the active graph.
 */
class JetpackNavigator : Navigator {

    @Volatile
    private var navController: NavController? = null

    fun attach(controller: NavController) {
        navController = controller
    }

    override fun navigate(to: AppDestination) {
        val controller = navController ?: return
        when (to) {
            is AppDestination.CharacterDetail -> {
                controller.navigate(
                    CharactersListFragmentDirections
                        .actionCharactersListFragmentToCharacterDetailFragment(to.characterId),
                )
            }
        }
    }

    override fun back() {
        navController?.navigateUp()
    }
}
