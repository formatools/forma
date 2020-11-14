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

package com.stepango.blockme.feature.characters.detail.impl.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepango.blockme.feature.characters.core.api.domain.repository.MarvelRepository
import com.stepango.blockme.feature.characters.detail.impl.model.CharacterDetail
import com.stepango.blockme.feature.characters.detail.impl.model.CharacterDetailMapper
import com.stepango.blockme.feature.characters.detail.impl.model.ICharacterDetail
import javax.inject.Inject
import kotlinx.coroutines.launch

/**
 * View model responsible for preparing and managing the data for [CharacterDetailFragment].
 *
 * @see ViewModel
 */
class CharacterDetailViewModel @Inject constructor(
    val marvelRepository: MarvelRepository,
// TODO Uncomment for favorite feature
//    val characterFavoriteRepository: CharacterFavoriteRepository,
    val characterDetailMapper: CharacterDetailMapper
) : ViewModel(), ICharacterDetailViewModel {

    private val _data = MutableLiveData<ICharacterDetail>()
    override val data: LiveData<ICharacterDetail>
        get() = _data

    private val _state = MutableLiveData<ICharacterDetailViewState>()
    override val state: LiveData<ICharacterDetailViewState>
        get() = _state

    // ============================================================================================
    //  Public methods
    // ============================================================================================

    /**
     * Fetch selected character detail info.
     *
     * @param characterId Character identifier.
     */
    override fun loadCharacterDetail(characterId: Long) {
        _state.postValue(CharacterDetailViewState.Loading)
        viewModelScope.launch {
            try {
                val result = marvelRepository.getCharacter(characterId)
                _data.postValue(characterDetailMapper.map(result))
// TODO Uncomment for favorite feature
//                characterFavoriteRepository.getCharacterFavorite(characterId)?.let {
//                    _state.postValue(CharacterDetailViewState.AlreadyAddedToFavorite)
//                } ?: run {
                    _state.postValue(CharacterDetailViewState.AddToFavorite)
//                }
            } catch (e: Exception) {
                _state.postValue(CharacterDetailViewState.Error)
            }
        }
    }

    /**
     * Store selected character to database favorite list.
     */
    override fun addCharacterToFavorite() {
// TODO Uncomment for favorite feature
//        _data.value?.let {
//            viewModelScope.launch {
//                characterFavoriteRepository.insertCharacterFavorite(
//                    id = it.id,
//                    name = it.name,
//                    imageUrl = it.imageUrl
//                )
//                _state.postValue(CharacterDetailViewState.AddedToFavorite)
//            }
//        }
    }

    /**
     * Send interaction event for dismiss character detail view.
     */
    override fun dismissCharacterDetail() {
        _state.postValue(CharacterDetailViewState.Dismiss)
    }
}
