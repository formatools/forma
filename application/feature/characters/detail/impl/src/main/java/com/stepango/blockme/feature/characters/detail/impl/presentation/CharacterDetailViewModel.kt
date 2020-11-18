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
import com.stepango.blockme.feature.characters.core.api.domain.repository.MarvelRepository
import com.stepango.blockme.feature.characters.detail.api.domain.model.ICharacterDetail
import com.stepango.blockme.feature.characters.detail.impl.data.mapper.CharacterDetailMapper
import com.stepango.blockme.feature.characters.favorite.api.domain.repository.ICharacterFavoriteRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class CharacterDetailViewModel @Inject constructor(
    private val marvelRepository: MarvelRepository,
    // TODO I'm a ViewModel and I can do anything! Give me more, plz!
    private val characterFavoriteRepository: ICharacterFavoriteRepository,
    private val characterDetailMapper: CharacterDetailMapper
) : ViewModel(), ICharacterDetailViewModel {

    private val _data = MutableLiveData<ICharacterDetail>()
    override val data: LiveData<ICharacterDetail>
        get() = _data

    private val _state = MutableLiveData<ICharacterDetailViewState>()
    override val state: LiveData<ICharacterDetailViewState>
        get() = _state

    override fun loadCharacterDetail(characterId: Long) {
        _state.postValue(CharacterDetailViewState.Loading)
        viewModelScope.launch {
            try {
                val result = marvelRepository.getCharacter(characterId)
                _data.postValue(characterDetailMapper.map(result))

                characterFavoriteRepository.getCharacterFavorite(characterId)?.let {
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
                characterFavoriteRepository.insertCharacterFavorite(
                    id = it.id,
                    name = it.name,
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
