package tools.forma.examples.android.navports.root

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import tools.forma.examples.android.navports.navigation.api.Navigator
import tools.forma.examples.android.navports.navigation.api.NavigatorProvider
import tools.forma.examples.android.navports.root.res.R as RootRes

/**
 * Composition root: inflates the NavHost and exposes [Navigator] to feature fragments.
 */
class NavPortsActivity : FragmentActivity(), NavigatorProvider {

    override lateinit var navigator: Navigator
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(RootRes.layout.activity_host)

        val navHost = supportFragmentManager.findFragmentById(RootRes.id.nav_host) as NavHostFragment
        navigator = JetpackNavigator(navHost.navController)
    }
}
