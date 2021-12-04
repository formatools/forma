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

import com.google.android.play.core.splitcompat.SplitCompatApplication
import com.stepango.blockme.common.util.clock.Clock
import com.stepango.blockme.core.di.library.BaseComponent
import com.stepango.blockme.core.di.library.BaseComponentProvider
import com.stepango.blockme.core.di.library.DaggerBaseComponent
import com.stepango.blockme.core.network.library.Config
import com.stepango.blockme.core.theme.android.util.ThemeUtils
import com.stepango.blockme.core.theme.android.util.di.DaggerThemeComponent
import com.stepango.blockme.core.theme.android.util.di.ThemeComponent
import com.stepango.blockme.core.theme.android.util.di.ThemeComponentProvider
import com.stepango.blockme.feature.characters.core.api.di.CharactersCoreApi
import com.stepango.blockme.feature.characters.core.api.di.CharactersCoreFeatureProvider
import com.stepango.blockme.feature.characters.core.impl.di.CharactersCoreComponent
import com.stepango.blockme.feature.characters.core.impl.di.DaggerCharactersCoreComponent
import com.stepango.blockme.feature.characters.detail.api.CharacterDetailsDepsStore
import com.stepango.blockme.feature.characters.favorite.api.CharacterFavoriteComponent
import com.stepango.blockme.feature.characters.favorite.api.DaggerCharacterFavoriteComponent
import com.stepango.blockme.root.library.deps.CharactersDetailsDepsImpl
import com.stepango.blockme.root.library.di.DaggerRootComponent
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

class SampleApp : SplitCompatApplication(),
    BaseComponentProvider,
    ThemeComponentProvider,
    CharactersCoreFeatureProvider {

    private lateinit var baseComponent: BaseComponent
    override fun getBaseComponent(): BaseComponent = baseComponent

    private lateinit var themeComponent: ThemeComponent
    override fun getThemeComponent(): ThemeComponent = themeComponent

    private lateinit var characterCoreComponent: CharactersCoreComponent
    override fun getCharactersCoreFeature(): CharactersCoreApi = characterCoreComponent

    private lateinit var characterFavoriteComponent: CharacterFavoriteComponent

    @Inject
    lateinit var themeUtils: ThemeUtils

    @Inject
    lateinit var config: Config

    @Inject
    lateinit var clock: Clock

    override fun onCreate() {
        super.onCreate()
        AppStartup.init(this)
        initTimber()
        initRootDependencyInjection()
        initRandomNightMode()
    }

    private fun initRootDependencyInjection() {
        baseComponent = DaggerBaseComponent.factory().create(this)
        themeComponent = DaggerThemeComponent.create()

        DaggerRootComponent
            .builder()
            .baseComponent(baseComponent)
            .themeComponent(themeComponent)
            .build()
            .inject(this)

        characterCoreComponent = DaggerCharactersCoreComponent.factory().create(config, clock)
        characterFavoriteComponent = DaggerCharacterFavoriteComponent.factory().create(baseComponent)
        CharacterDetailsDepsStore.deps = CharactersDetailsDepsImpl(characterFavoriteComponent)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initRandomNightMode() {
        if (BuildConfig.DEBUG) {
            themeUtils.setNightMode(Random.nextBoolean())
        }
    }
}
