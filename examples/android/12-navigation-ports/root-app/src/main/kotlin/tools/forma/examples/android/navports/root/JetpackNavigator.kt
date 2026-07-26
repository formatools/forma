package tools.forma.examples.android.navports.root

import androidx.navigation.NavController
import tools.forma.examples.android.navports.feature.list.impl.ListFragmentDirections
import tools.forma.examples.android.navports.navigation.api.AppDestination
import tools.forma.examples.android.navports.navigation.api.Navigator

/**
 * Sole module that may import NavController + Safe Args *Directions.
 * Feature `impl` never sees these types — Directions are generated into the
 * fragment's package name from XML, but compiled only in `navigation/res`.
 */
class JetpackNavigator(
    private val navController: NavController,
) : Navigator {

    override fun navigate(to: AppDestination) {
        when (to) {
            is AppDestination.ItemDetail -> {
                navController.navigate(
                    ListFragmentDirections.actionListToDetail(to.itemId),
                )
            }
        }
    }

    override fun back() {
        navController.popBackStack()
    }
}
