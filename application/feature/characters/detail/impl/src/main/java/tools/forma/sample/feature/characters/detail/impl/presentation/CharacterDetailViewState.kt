/*
 * Copyright 2019 forma.tools
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

package tools.forma.sample.feature.characters.detail.impl.presentation

import tools.forma.sample.core.mvvm.library.ui.BaseViewState
import tools.forma.sample.feature.characters.detail.api.presentation.ICharacterDetailViewState

sealed class CharacterDetailViewState : BaseViewState, ICharacterDetailViewState {

    object Loading : CharacterDetailViewState()

    object Error : CharacterDetailViewState()

    object AddToFavorite : CharacterDetailViewState()

    object AddedToFavorite : CharacterDetailViewState()

    object AlreadyAddedToFavorite : CharacterDetailViewState()

    object Dismiss : CharacterDetailViewState()

    override fun isLoading() = this is Loading

    override fun isError() = this is Error

    override fun isAddToFavorite() = this is AddToFavorite

    override fun isAddedToFavorite() = this is AddedToFavorite

    override fun isAlreadyAddedToFavorite() = this is AlreadyAddedToFavorite

    override fun isDismiss() = this is Dismiss
}
