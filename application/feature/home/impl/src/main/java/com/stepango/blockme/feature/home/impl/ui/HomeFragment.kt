/*
 * Copyright 2019 vmadalin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stepango.blockme.feature.home.impl.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.stepango.blockme.common.extensions.android.util.setupWithNavController
import com.stepango.blockme.core.mvvm.library.ui.BaseFragment
import com.stepango.blockme.core.mvvm.library.viewModels
import com.stepango.blockme.core.theme.android.util.ThemeUtils
import com.stepango.blockme.core.theme.android.util.di.ThemeComponentProvider
import com.stepango.blockme.feature.home.databinding.databinding.FragmentHomeBinding
import com.stepango.blockme.feature.home.impl.R
import com.stepango.blockme.feature.home.impl.di.DaggerHomeComponent
import com.stepango.blockme.feature.home.impl.ui.menu.ToggleThemeCheckBox
import javax.inject.Inject

private const val DELAY_TO_APPLY_THEME = 1000L

class HomeFragment : BaseFragment<FragmentHomeBinding>(
    layoutId = R.layout.fragment_home
) {

    @Inject
    lateinit var themeUtils: ThemeUtils

    private val viewModel: HomeViewModel by viewModels()

    private val navGraphIds = listOf(
        R.navigation.navigation_characters_list_graph,
        R.navigation.navigation_character_favorite_graph
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        setupBottomNavigationBar()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu, menu)

        menu.findItem(R.id.menu_toggle_theme).apply {
            val actionView = this.actionView
            if (actionView is ToggleThemeCheckBox) {
                actionView.isChecked = themeUtils.isDarkTheme(requireContext())
                actionView.setOnCheckedChangeListener { _, isChecked ->
                    themeUtils.setNightMode(isChecked, DELAY_TO_APPLY_THEME)
                }
            }
        }
        menu.findItem(R.id.menu_app_info).apply {
            setOnMenuItemClickListener {
                try {
                    // Correct way to take app version code and version name, instead of BuildConfig
                    val packageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
                    val versionName = packageInfo.versionName
                    val versionCode = packageInfo.versionCode
                    Toast
                        .makeText(
                            requireContext(),
                            getString(R.string.home_app_info_template, versionName, versionCode),
                            Toast.LENGTH_LONG
                        )
                        .show()
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
                true
            }
        }
    }

    override fun onInitDependencyInjection() {
        DaggerHomeComponent
            .factory()
            .create(
                requireProvider(ThemeComponentProvider::class).getThemeComponent()
            )
            .inject(this)
    }

    override fun onInitDataBinding() {
        viewBinding.viewModel = viewModel
    }

    private fun setupToolbar() {
        setHasOptionsMenu(true)
        requireCompatActivity().setSupportActionBar(viewBinding.toolbar)
    }

    private fun setupBottomNavigationBar() {
        // TODO https://github.com/formatools/forma/issues/46
        // Need abstract navigation layer here
        val navController = viewBinding.bottomNavigation.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = childFragmentManager,
            containerId = R.id.nav_host_container,
            intent = requireActivity().intent
        )

        navController.observe(viewLifecycleOwner, {
            viewModel.navigationControllerChanged(it)
            setupActionBarWithNavController(requireCompatActivity(), it)
        })
    }
}
