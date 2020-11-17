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

package com.stepango.blockme.feature.characters.favorite.impl.presentation

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepango.blockme.feature.characters.favorite.impl.domain.entity.CharacterFavorite
import com.stepango.blockme.feature.characters.favorite.impl.domain.repository.CharacterFavoriteRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class CharacterFavoriteViewModel @Inject constructor(
    private val characterFavoriteRepository: CharacterFavoriteRepository
) : ViewModel() {

    val data = characterFavoriteRepository.getAllCharactersFavoriteLiveData()
    val state = Transformations.map(data) {
        if (it.isEmpty()) {
            CharacterFavoriteViewState.Empty
        } else {
            CharacterFavoriteViewState.Listed
        }
    }

    fun removeFavoriteCharacter(character: CharacterFavorite) {
        viewModelScope.launch {
            characterFavoriteRepository.deleteCharacterFavorite(character)
        }
    }
}
