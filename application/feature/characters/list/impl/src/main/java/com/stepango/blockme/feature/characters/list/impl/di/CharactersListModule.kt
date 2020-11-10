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

package com.stepango.blockme.feature.characters.list.impl.di

import androidx.lifecycle.ViewModel
import com.stepango.blockme.core.di.library.scopes.FeatureScope
import com.stepango.blockme.core.mvvm.library.di.ViewModelKey
import com.stepango.blockme.feature.characters.core.api.domain.repository.MarvelRepository
import com.stepango.blockme.feature.characters.list.impl.data.datasource.CharacterPageDataSource
import com.stepango.blockme.feature.characters.list.impl.data.mapper.CharacterItemMapper
import com.stepango.blockme.feature.characters.list.impl.ui.CharactersListViewModel
import com.stepango.blockme.feature.characters.list.impl.ui.adapter.CharactersListAdapter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

@Module
internal abstract class CharactersListModule {

    @Binds
    @IntoMap
    @ViewModelKey(CharactersListViewModel::class)
    abstract fun bindsCharactersListViewModel(viewModel: CharactersListViewModel): ViewModel

    companion object {

        @FeatureScope
        @Provides
        fun providesCharactersPageDataSource(
            repository: MarvelRepository,
            mapper: CharacterItemMapper
        ) = CharacterPageDataSource(
            repository = repository,
            mapper = mapper
        )

        @FeatureScope
        @Provides
        fun providesCharacterItemMapper() = CharacterItemMapper()
    }
}
