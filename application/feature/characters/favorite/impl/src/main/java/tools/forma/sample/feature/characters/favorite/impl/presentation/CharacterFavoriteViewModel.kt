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

package tools.forma.sample.feature.characters.favorite.impl.presentation

import androidx.lifecycle.*
import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter
import tools.forma.sample.feature.characters.favorite.api.domain.usecase.IDeleteCharacterFavoriteUseCase
import tools.forma.sample.feature.characters.favorite.api.domain.usecase.IGetAllCharactersFavoriteUseCase
import tools.forma.sample.feature.characters.favorite.viewbinding.presentation.ICharacterFavoriteViewModel
import tools.forma.sample.feature.characters.favorite.viewbinding.presentation.ICharacterFavoriteViewState
import tools.forma.sample.feature.characters.favorite.impl.presentation.CharacterFavoriteViewState.Empty
import tools.forma.sample.feature.characters.favorite.impl.presentation.CharacterFavoriteViewState.Listed
import kotlinx.coroutines.launch
import javax.inject.Inject

class CharacterFavoriteViewModel @Inject constructor(
    private val getAllCharactersFavoriteUseCase: IGetAllCharactersFavoriteUseCase,
    private val deleteCharacterFavoriteUseCase: IDeleteCharacterFavoriteUseCase
) : ICharacterFavoriteViewModel, ViewModel() {

    private val _data = MutableLiveData<List<ICharacter>>()
    override val data: LiveData<List<ICharacter>>
        get() = _data

    override val state: LiveData<ICharacterFavoriteViewState>
        get() = Transformations.map(_data) {
            if (it.isEmpty()) {
                Empty
            } else {
                Listed
            }
        }

    override fun loadFavoriteCharacters() {
        viewModelScope.launch {
            val result = getAllCharactersFavoriteUseCase()
            _data.postValue(result)
        }
    }

    override fun removeFavoriteCharacter(character: ICharacter) {
        viewModelScope.launch {
            deleteCharacterFavoriteUseCase(character)
        }
    }
}
