package tools.forma.sample.core.navigation.android.util

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Nav-aware host surface for the home composition shell (multi-backstack bottom nav).
 *
 * HomeFragment depends on this port instead of graph R.ids / NavController directly.
 * Jetpack implementation lives at the composition root (`root-app`).
 *
 * Kept free of Layer A `api` ports so this `androidUtil` stays matrix-valid
 * (`androidUtil` ↛ `api`). Chrome is reported as a boolean; home maps it to
 * [tools.forma.sample.core.navigation.api.HomeChromeMode] / HomeViewState.
 */
interface HomeShellNavigation {

    /**
     * Wire multi-backstack bottom navigation + action bar.
     *
     * @param onNavigationScreen true when the active destination is a tab root
     *   (show app bar + bottom nav); false for full-screen destinations.
     */
    fun bind(
        activity: AppCompatActivity,
        bottomNavigationView: BottomNavigationView,
        fragmentManager: FragmentManager,
        containerId: Int,
        intent: Intent,
        lifecycleOwner: LifecycleOwner,
        onNavigationScreen: (Boolean) -> Unit,
    )
}

/** Application exposes the home-shell binder like other feature providers. */
interface HomeShellNavigationProvider {
    fun getHomeShellNavigation(): HomeShellNavigation
}
