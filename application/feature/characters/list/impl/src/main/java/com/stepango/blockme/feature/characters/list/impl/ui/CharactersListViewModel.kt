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

package com.stepango.blockme.feature.characters.list.impl.ui

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import com.stepango.blockme.core.mvvm.library.lifecycle.SingleLiveData
import com.stepango.blockme.core.network.library.NetworkState
import com.stepango.blockme.feature.characters.list.impl.data.datasource.CharactersPageDataSourceFactory
import com.stepango.blockme.feature.characters.list.impl.data.datasource.PAGE_MAX_ELEMENTS
import javax.inject.Inject

/**
 * View model responsible for preparing and managing the data for [CharactersListFragment].
 *
 * @see ViewModel
 */
class CharactersListViewModel @Inject constructor(
    private val dataSourceFactory: CharactersPageDataSourceFactory
) : ViewModel() {

    val networkState = Transformations.switchMap(dataSourceFactory.sourceLiveData) {
        it.networkState
    }

    val event = SingleLiveData<CharactersListViewEvent>()
    val data = LivePagedListBuilder(dataSourceFactory, PAGE_MAX_ELEMENTS).build()
    val state = Transformations.map(networkState) {
        when (it) {
            is NetworkState.Success ->
                if (it.isAdditional && it.isEmptyResponse) {
                    CharactersListViewState.NoMoreElements
                } else if (it.isEmptyResponse) {
                    CharactersListViewState.Empty
                } else {
                    CharactersListViewState.Loaded
                }
            is NetworkState.Loading ->
                if (it.isAdditional) {
                    CharactersListViewState.AddLoading
                } else {
                    CharactersListViewState.Loading
                }
            is NetworkState.Error ->
                if (it.isAdditional) {
                    CharactersListViewState.AddError
                } else {
                    CharactersListViewState.Error
                }
        }
    }

    // ============================================================================================
    //  Public methods
    // ============================================================================================

    /**
     * Refresh characters fetch them again and update the list.
     */
    fun refreshLoadedCharactersList() {
        dataSourceFactory.refresh()
    }

    /**
     * Retry last fetch operation to add characters into list.
     */
    fun retryAddCharactersList() {
        dataSourceFactory.retry()
    }

    /**
     * Send interaction event for open character detail view from selected character.
     *
     * @param characterId Character identifier.
     */
    fun openCharacterDetail(characterId: Long) {
        event.postValue(CharactersListViewEvent.OpenCharacterDetail(characterId))
    }
}
