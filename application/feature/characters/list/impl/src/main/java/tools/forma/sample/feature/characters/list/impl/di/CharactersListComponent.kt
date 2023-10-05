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

package tools.forma.sample.feature.characters.list.impl.di

import tools.forma.sample.core.di.library.scopes.FeatureScope
import tools.forma.sample.feature.characters.core.api.di.CharactersCoreFeature
import tools.forma.sample.feature.characters.list.impl.ui.CharactersListFragment
import dagger.Component

@FeatureScope
@Component(
    modules = [CharactersListModule::class],
    dependencies = [CharactersCoreFeature::class])
internal interface CharactersListComponent {

    fun inject(listFragment: CharactersListFragment)

    @Component.Factory
    interface Factory {

        fun create(charactersCoreFeature: CharactersCoreFeature): CharactersListComponent
    }
}
