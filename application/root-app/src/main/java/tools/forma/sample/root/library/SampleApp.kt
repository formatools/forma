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

package tools.forma.sample.root.library

import com.google.android.play.core.splitcompat.SplitCompatApplication
import tools.forma.sample.common.util.clock.Clock
import tools.forma.sample.core.di.library.BaseComponent
import tools.forma.sample.core.di.library.BaseComponentProvider
import tools.forma.sample.core.di.library.DaggerBaseComponent
import tools.forma.sample.core.network.library.Config
import tools.forma.sample.core.theme.android.util.ThemeUtils
import tools.forma.sample.core.theme.android.util.di.DaggerThemeComponent
import tools.forma.sample.core.theme.android.util.di.ThemeComponent
import tools.forma.sample.core.theme.android.util.di.ThemeComponentProvider
import tools.forma.sample.feature.characters.core.api.di.CharactersCoreFeature
import tools.forma.sample.feature.characters.core.api.di.CharactersCoreFeatureProvider
import tools.forma.sample.feature.characters.core.impl.di.CharactersCoreComponent
import tools.forma.sample.feature.characters.core.impl.di.DaggerCharactersCoreComponent
import tools.forma.sample.feature.characters.favorite.api.di.CharacterFavoriteFeature
import tools.forma.sample.feature.characters.favorite.api.di.CharacterFavoriteFeatureProvider
import tools.forma.sample.feature.characters.favorite.impl.di.CharacterFavoriteComponent
import tools.forma.sample.feature.characters.favorite.impl.di.DaggerCharacterFavoriteComponent
import tools.forma.sample.root.library.di.DaggerRootComponent
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

class SampleApp : SplitCompatApplication(),
    BaseComponentProvider,
    ThemeComponentProvider,
    CharactersCoreFeatureProvider,
    CharacterFavoriteFeatureProvider {

    private lateinit var baseComponent: BaseComponent
    override fun getBaseComponent(): BaseComponent = baseComponent

    private lateinit var themeComponent: ThemeComponent
    override fun getThemeComponent(): ThemeComponent = themeComponent

    private lateinit var characterCoreComponent: CharactersCoreComponent
    override fun getCharactersCoreFeature(): CharactersCoreFeature = characterCoreComponent

    private lateinit var characterFavoriteComponent: CharacterFavoriteComponent
    override fun getCharacterFavoriteFeature(): CharacterFavoriteFeature = characterFavoriteComponent

    @Inject
    lateinit var config: Config

    @Inject
    lateinit var clock: Clock

    override fun onCreate() {
        super.onCreate()
        initRootDependencyInjection()
    }

    private fun initRootDependencyInjection() {
        baseComponent = DaggerBaseComponent.factory().create(this)
        themeComponent = DaggerThemeComponent.factory().create()

        DaggerRootComponent
            .builder()
            .baseComponent(baseComponent)
            .themeComponent(themeComponent)
            .build()
            .inject(this)

        characterCoreComponent = DaggerCharactersCoreComponent.factory().create(config, clock)
        characterFavoriteComponent = DaggerCharacterFavoriteComponent.factory().create(baseComponent)
    }
}
