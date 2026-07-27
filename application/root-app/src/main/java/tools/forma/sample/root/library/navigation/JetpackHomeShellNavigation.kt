package tools.forma.sample.root.library.navigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import tools.forma.sample.core.navigation.android.util.HomeShellNavigation
import tools.forma.sample.core.navigation.android.util.setupWithNavController
import tools.forma.sample.core.navigation.library.R as NavR

/**
 * Jetpack multi-backstack bottom-nav + action-bar wiring for the home shell.
 * Owns graph R.ids and NavController listeners; reports only a boolean chrome flag.
 */
class JetpackHomeShellNavigation(
    private val jetpackNavigator: JetpackNavigator,
) : HomeShellNavigation {

    private val navGraphIds = listOf(
        NavR.navigation.navigation_characters_list_graph,
        NavR.navigation.navigation_character_favorite_graph,
    )

    private val tabRootDestinationIds = setOf(
        NavR.id.characters_list_fragment,
        NavR.id.character_favorite_fragment,
    )

    override fun bind(
        activity: AppCompatActivity,
        bottomNavigationView: BottomNavigationView,
        fragmentManager: FragmentManager,
        containerId: Int,
        intent: Intent,
        lifecycleOwner: LifecycleOwner,
        onNavigationScreen: (Boolean) -> Unit,
    ) {
        val selectedNavController = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = fragmentManager,
            containerId = containerId,
            intent = intent,
        )

        selectedNavController.observe(lifecycleOwner) { navController ->
            jetpackNavigator.attach(navController)
            setupActionBarWithNavController(activity, navController)
            navController.addOnDestinationChangedListener { _, destination, _ ->
                onNavigationScreen(tabRootDestinationIds.contains(destination.id))
            }
            // Emit current destination chrome immediately.
            navController.currentDestination?.id?.let { id ->
                onNavigationScreen(tabRootDestinationIds.contains(id))
            }
        }
    }
}
