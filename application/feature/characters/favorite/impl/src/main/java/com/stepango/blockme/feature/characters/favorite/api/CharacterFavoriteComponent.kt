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

package com.stepango.blockme.feature.characters.favorite.api

import androidx.fragment.app.Fragment
import com.stepango.blockme.core.di.library.BaseComponent
import com.stepango.blockme.core.di.library.scopes.FeatureScope
import com.stepango.blockme.feature.characters.favorite.impl.di.CharacterFavoriteModule
import com.stepango.blockme.feature.characters.favorite.impl.di.MarvelModule
import com.stepango.blockme.feature.characters.favorite.impl.ui.CharacterFavoriteFragment
import dagger.Component
import javax.inject.Singleton

@FeatureScope
@Singleton
@Component(
    modules = [
        CharacterFavoriteModule::class,
        MarvelModule::class
    ],
    dependencies = [
        BaseComponent::class
    ]
)
abstract class CharacterFavoriteComponent : CharactersFavoriteApi {

    internal abstract fun inject(favoriteFragment: CharacterFavoriteFragment)

    override fun getFragment(): Fragment = CharacterFavoriteFragment()

    @Component.Factory
    interface Factory {

        fun create(baseComponent: BaseComponent): CharacterFavoriteComponent
    }
}
