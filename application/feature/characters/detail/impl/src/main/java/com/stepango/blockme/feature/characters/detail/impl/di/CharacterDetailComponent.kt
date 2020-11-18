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

package com.stepango.blockme.feature.characters.detail.impl.di

import com.stepango.blockme.core.di.library.scopes.FeatureScope
import com.stepango.blockme.feature.characters.core.api.di.CharactersCoreFeature
import com.stepango.blockme.feature.characters.detail.impl.ui.CharacterDetailFragment
import com.stepango.blockme.feature.characters.favorite.api.di.CharacterFavoriteFeature
import dagger.Component

@FeatureScope
@Component(
    modules = [
        CharacterDetailModule::class
    ],
    dependencies = [
        CharactersCoreFeature::class,
        CharacterFavoriteFeature::class,
    ]
)
internal interface CharacterDetailComponent {

    fun inject(detailFragment: CharacterDetailFragment)

    @Component.Factory
    interface Factory {

        fun create(
            charactersCoreFeature: CharactersCoreFeature,
            characterFavoriteFeature: CharacterFavoriteFeature
        ): CharacterDetailComponent
    }
}
