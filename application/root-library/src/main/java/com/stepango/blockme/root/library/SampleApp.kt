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

package com.stepango.blockme.root.library

import android.content.Context
import com.google.android.play.core.splitcompat.SplitCompatApplication
import com.stepango.blockme.core.theme.android.util.ThemeUtils
import com.stepango.blockme.core.theme.android.util.di.DaggerThemeComponent
import com.stepango.blockme.core.theme.android.util.di.ThemeComponent
import com.stepango.blockme.root.library.di.DaggerRootComponent
import com.stepango.blockme.root.library.di.RootComponent
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

/**
 * Base class for maintaining global application state.
 *
 * @see SplitCompatApplication
 */
class SampleApp : SplitCompatApplication() {

    private lateinit var rootComponent: RootComponent
    private lateinit var themeComponent: ThemeComponent

    @Inject
    lateinit var themeUtils: ThemeUtils

    companion object {

        /**
         * Obtain core dagger component.
         *
         * @param context The application context
         */
        @JvmStatic
        fun rootComponent(context: Context) =
            (context.applicationContext as? SampleApp)?.rootComponent
    }

    /**
     * Called when the application is starting, before any activity, service, or receiver objects
     * (excluding content providers) have been created.
     *
     * @see SplitCompatApplication.onCreate
     */
    override fun onCreate() {
        super.onCreate()
        initTimber()
        initThemeUtilsDependencyInjection()
        initRootDependencyInjection()
        initRandomNightMode()
    }

    // ============================================================================================
    //  Private init methods
    // ============================================================================================

    /**
     * Initialize root dependency injection component.
     */
    private fun initRootDependencyInjection() {
        rootComponent = DaggerRootComponent
            .builder()
            .themeComponent(themeComponent)
            .build()

        rootComponent.inject(this)
    }

    private fun initThemeUtilsDependencyInjection() {
        themeComponent = DaggerThemeComponent
            .builder()
            .build()
    }

    /**
     * Initialize log library Timber only on debug build.
     */
    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    /**
     * Initialize random nightMode to make developer aware of day/night themes.
     */
    private fun initRandomNightMode() {
        if (BuildConfig.DEBUG) {
            themeUtils.setNightMode(Random.nextBoolean())
        }
    }
}
