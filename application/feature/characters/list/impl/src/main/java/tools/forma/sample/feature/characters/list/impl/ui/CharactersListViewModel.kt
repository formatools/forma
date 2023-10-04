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

package tools.forma.sample.feature.characters.list.impl.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import tools.forma.sample.core.mvvm.library.lifecycle.SingleLiveData
import tools.forma.sample.core.network.library.NetworkState
import tools.forma.sample.feature.characters.list.impl.data.datasource.CharactersPageDataSourceFactory
import tools.forma.sample.feature.characters.list.impl.data.datasource.PAGE_MAX_ELEMENTS
import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter
import tools.forma.sample.feature.characters.list.impl.domain.model.ICharactersListViewEvent
import tools.forma.sample.feature.characters.list.impl.domain.model.ICharactersListViewModel
import tools.forma.sample.feature.characters.list.impl.domain.model.ICharactersListViewState
import javax.inject.Inject

class CharactersListViewModel @Inject constructor(
    // TODO https://github.com/formatools/forma/issues/48
    // Aggregate UseCase here
    private val dataSourceFactory: CharactersPageDataSourceFactory
) : ViewModel(), ICharactersListViewModel {

    override val networkState = Transformations.switchMap(dataSourceFactory.sourceLiveData) {
        it.networkState
    }

    override val event = SingleLiveData<ICharactersListViewEvent>()
    override val data: LiveData<PagedList<ICharacter>> = LivePagedListBuilder(dataSourceFactory, PAGE_MAX_ELEMENTS).build()
    override val state: LiveData<ICharactersListViewState> = Transformations.map(networkState) {
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

    override fun refreshLoadedCharactersList() {
        dataSourceFactory.refresh()
    }

    override fun retryAddCharactersList() {
        dataSourceFactory.retry()
    }

    override fun openCharacterDetail(characterId: Long) {
        event.postValue(CharactersListViewEvent.OpenCharacterDetail(characterId))
    }
}
