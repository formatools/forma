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

package com.stepango.blockme.feature.characters.favorite.impl.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import com.stepango.blockme.common.extensions.android.util.observe
import com.stepango.blockme.core.di.library.BaseComponentProvider
import com.stepango.blockme.core.mvvm.library.ui.BaseFragment
import com.stepango.blockme.core.mvvm.library.viewModels
import com.stepango.blockme.feature.characters.core.api.domain.model.ICharacter
import com.stepango.blockme.feature.characters.favorite.databinding.presentation.ICharacterFavoriteViewModel
import com.stepango.blockme.feature.characters.favorite.impl.R
import com.stepango.blockme.feature.characters.favorite.impl.di.DaggerCharacterFavoriteComponent
import com.stepango.blockme.feature.characters.favorite.impl.ui.adapter.CharacterFavoriteAdapter
import com.stepango.blockme.feature.characters.favorite.impl.ui.adapter.CharacterFavoriteTouchHelper
import com.stepango.blockme.feature.favorite.databinding.databinding.FragmentCharacterFavoriteListBinding

class CharacterFavoriteFragment :
    BaseFragment<FragmentCharacterFavoriteListBinding>(
        layoutId = R.layout.fragment_character_favorite_list
    ) {

    private val viewModel: ICharacterFavoriteViewModel by viewModels()

    private val viewAdapter = CharacterFavoriteAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(viewModel.data, ::onViewDataChange)

        viewModel.loadFavoriteCharacters()
    }

    override fun onInitDependencyInjection() {
        DaggerCharacterFavoriteComponent
            .factory()
            .create(requireProvider(BaseComponentProvider::class).getBaseComponent())
            .inject(this)
    }

    override fun onInitDataBinding() {
        viewBinding.viewModel = viewModel
        viewBinding.includeList.charactersFavoriteList.apply {
            adapter = viewAdapter

            ItemTouchHelper(CharacterFavoriteTouchHelper {
                viewModel.removeFavoriteCharacter(viewAdapter.currentList[it])
            }).attachToRecyclerView(this)
        }
    }

    private fun onViewDataChange(viewData: List<ICharacter>) {
        viewAdapter.submitList(viewData)
    }
}
