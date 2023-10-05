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

package tools.forma.sample.feature.characters.list.impl.data.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import tools.forma.sample.core.network.library.NetworkState
import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter
import tools.forma.sample.feature.characters.core.api.domain.repository.MarvelRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

const val PAGE_INIT_ELEMENTS = 0
const val PAGE_MAX_ELEMENTS = 50

/**
 * Incremental data loader for page-keyed content, where requests return keys for next/previous
 * pages. Obtaining paginated the Marvel characters.
 *
 * @see PageKeyedDataSource
 */
// TODO https://github.com/formatools/forma/issues/48
// Rewrite on clean version with separate Repository with local/remote datasource
open class CharacterPageDataSource(
    private val repository: MarvelRepository,
) : PageKeyedDataSource<Int, ICharacter>() {

    val networkState = MutableLiveData<NetworkState>()
    private var retry: (() -> Unit)? = null

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, ICharacter>
    ) {
        networkState.postValue(NetworkState.Loading())
        // TODO https://github.com/formatools/forma/issues/48
        // Don't do that!!! Using GlobalScope here only for first working version
        // Make it from UseCase and calling it from View Model scope
        GlobalScope.launch(CoroutineExceptionHandler { _, _ ->
            retry = {
                loadInitial(params, callback)
            }
            networkState.postValue(NetworkState.Error())
        }) {
            val response = repository.getCharacters(
                offset = PAGE_INIT_ELEMENTS,
                limit = PAGE_MAX_ELEMENTS
            )
            callback.onResult(response, null, PAGE_MAX_ELEMENTS)
            networkState.postValue(NetworkState.Success(isEmptyResponse = response.isEmpty()))
        }
    }

    /**
     * Append page with the key specified by [LoadParams.key].
     *
     * @param params Parameters for the load, including the key for the new page, and requested
     * load size.
     * @param callback Callback that receives loaded data.
     * @see PageKeyedDataSource.loadAfter
     */
    override fun loadAfter(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, ICharacter>
    ) {
        networkState.postValue(NetworkState.Loading(true))
        // TODO https://github.com/formatools/forma/issues/48
        // Don't do that!!! Using GlobalScope here only for first working version
        // Make it from UseCase and calling it from View Model scope
        GlobalScope.launch(CoroutineExceptionHandler { _, _ ->
            retry = {
                loadAfter(params, callback)
            }
            networkState.postValue(NetworkState.Error(true))
        }) {
            val response = repository.getCharacters(
                offset = params.key,
                limit = PAGE_MAX_ELEMENTS
            )
            callback.onResult(response, params.key + PAGE_MAX_ELEMENTS)
            networkState.postValue(NetworkState.Success(true, response.isEmpty()))
        }
    }

    /**
     * Prepend page with the key specified by [LoadParams.key]
     *
     * @param params Parameters for the load, including the key for the new page, and requested
     * load size.
     * @param callback Callback that receives loaded data.
     * @see PageKeyedDataSource.loadBefore
     */
    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, ICharacter>
    ) {
        // Ignored, since we only ever append to our initial load
    }

    /**
     * Force retry last fetch operation in case it has ever been previously executed.
     */
    fun retry() {
        retry?.invoke()
    }
}
