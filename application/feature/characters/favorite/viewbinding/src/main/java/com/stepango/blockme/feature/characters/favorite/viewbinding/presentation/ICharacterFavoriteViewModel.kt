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

package com.stepango.blockme.feature.characters.favorite.viewbinding.presentation

import androidx.lifecycle.LiveData
import com.stepango.blockme.feature.characters.core.api.domain.model.ICharacter

interface ICharacterFavoriteViewModel {

    val data: LiveData<List<ICharacter>>

    val state: LiveData<ICharacterFavoriteViewState>

    fun loadFavoriteCharacters()

    fun removeFavoriteCharacter(character: ICharacter)
}

interface ICharacterFavoriteViewState {

    fun isEmpty(): Boolean

    fun isListed(): Boolean
}

