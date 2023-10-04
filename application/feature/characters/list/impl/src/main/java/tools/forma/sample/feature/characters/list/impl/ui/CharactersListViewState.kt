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

import tools.forma.sample.core.mvvm.library.ui.BaseViewState
import tools.forma.sample.feature.characters.list.impl.domain.model.ICharactersListViewState

sealed class CharactersListViewState : BaseViewState, ICharactersListViewState {

    object Refreshing : CharactersListViewState()

    object Loaded : CharactersListViewState()

    object Loading : CharactersListViewState()

    object AddLoading : CharactersListViewState()

    object Empty : CharactersListViewState()

    object Error : CharactersListViewState()

    object AddError : CharactersListViewState()

    object NoMoreElements : CharactersListViewState()

    override fun isRefreshing() = this is Refreshing

    override fun isLoaded() = this is Loaded

    override fun isLoading() = this is Loading

    override fun isAddLoading() = this is AddLoading

    override fun isEmpty() = this is Empty

    override fun isError() = this is Error

    override fun isAddError() = this is AddError

    override fun isNoMoreElements() = this is NoMoreElements
}
