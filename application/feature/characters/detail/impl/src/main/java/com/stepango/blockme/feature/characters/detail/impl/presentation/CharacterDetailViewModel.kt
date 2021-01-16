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

package com.stepango.blockme.feature.characters.detail.impl.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepango.blockme.feature.characters.core.api.data.mapper.ICharacterMapper
import com.stepango.blockme.feature.characters.core.api.domain.model.ICharacter
import com.stepango.blockme.feature.characters.core.api.domain.repository.MarvelRepository
import com.stepango.blockme.feature.characters.favorite.api.domain.usecase.IGetCharacterFavoriteUseCase
import com.stepango.blockme.feature.characters.favorite.api.domain.usecase.ISetCharacterFavoriteUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class CharacterDetailViewModel @Inject constructor(
        private val marvelRepository: MarvelRepository,
        private val getCharacterFavoriteUseCase: IGetCharacterFavoriteUseCase,
        private val setCharacterFavoriteUseCase: ISetCharacterFavoriteUseCase,
        private val characterDetailMapper: ICharacterMapper,
) : ViewModel(), ICharacterDetailViewModel {

    private val _data = MutableLiveData<ICharacter>()
    override val data: LiveData<ICharacter>
        get() = _data

    private val _state = MutableLiveData<ICharacterDetailViewState>()
    override val state: LiveData<ICharacterDetailViewState>
        get() = _state

    override fun loadCharacterDetail(characterId: Long) {
        _state.postValue(CharacterDetailViewState.Loading)
        viewModelScope.launch {
            try {
                val result = marvelRepository.getCharacter(characterId)
                _data.postValue(characterDetailMapper.map(result).first())

                getCharacterFavoriteUseCase(characterId)?.let {
                    _state.postValue(CharacterDetailViewState.AlreadyAddedToFavorite)
                } ?: run {
                    _state.postValue(CharacterDetailViewState.AddToFavorite)
                }
            } catch (e: Exception) {
                _state.postValue(CharacterDetailViewState.Error)
            }
        }
    }

    override fun addCharacterToFavorite() {
        _data.value?.let {
            viewModelScope.launch {
                setCharacterFavoriteUseCase(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    imageUrl = it.imageUrl
                )
                _state.postValue(CharacterDetailViewState.AddedToFavorite)
            }
        }
    }

    override fun dismissCharacterDetail() {
        _state.postValue(CharacterDetailViewState.Dismiss)
    }
}
