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

package tools.forma.sample.feature.characters.favorite.impl.ui

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import by.kirich1409.viewbindingdelegate.viewBinding
import tools.forma.sample.common.extensions.android.util.observe
import tools.forma.sample.common.recyclerview.widget.RecyclerViewItemDecoration
import tools.forma.sample.core.di.library.BaseComponentProvider
import tools.forma.sample.core.mvvm.library.ui.BaseViewBindingFragment
import tools.forma.sample.core.mvvm.library.viewModels
import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter
import tools.forma.sample.feature.characters.favorite.viewbinding.presentation.ICharacterFavoriteViewModel
import tools.forma.sample.feature.characters.favorite.viewbinding.presentation.ICharacterFavoriteViewState
import tools.forma.sample.feature.characters.favorite.impl.R
import tools.forma.sample.feature.characters.favorite.impl.di.DaggerCharacterFavoriteComponent
import tools.forma.sample.feature.characters.favorite.impl.ui.adapter.CharacterFavoriteAdapter
import tools.forma.sample.feature.characters.favorite.impl.ui.adapter.CharacterFavoriteTouchHelper
import tools.forma.sample.feature.favorite.viewbinding.databinding.FragmentCharacterFavoriteListBinding

class CharacterFavoriteFragment : BaseViewBindingFragment(
    layoutId = tools.forma.sample.feature.favorite.viewbinding.R.layout.fragment_character_favorite_list
) {

    private val viewModel: ICharacterFavoriteViewModel by viewModels()
    private val viewBinding: FragmentCharacterFavoriteListBinding by viewBinding(FragmentCharacterFavoriteListBinding::bind)

    private val viewAdapter = CharacterFavoriteAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observe(viewModel.data, ::onViewDataChange)
        observe(viewModel.state, ::onViewStateChange)

        viewModel.loadFavoriteCharacters()
    }

    override fun onInitDependencyInjection() {
        DaggerCharacterFavoriteComponent
            .factory()
            .create(requireProvider(BaseComponentProvider::class).getBaseComponent())
            .inject(this)
    }

    private fun setupRecyclerView() {
        viewBinding.includeList.charactersFavoriteList.apply {
            addItemDecoration(
                RecyclerViewItemDecoration(
                    resources.getDimensionPixelSize(tools.forma.sample.feature.character.favorite.res.R.dimen.characters_favorite_list_item_padding)
                )
            )
            adapter = viewAdapter

            ItemTouchHelper(CharacterFavoriteTouchHelper {
                viewModel.removeFavoriteCharacter(viewAdapter.currentList[it])
            }).attachToRecyclerView(this)
        }
    }

    private fun onViewDataChange(viewData: List<ICharacter>) {
        viewAdapter.submitList(viewData)
    }

    private fun onViewStateChange(viewState: ICharacterFavoriteViewState) {
        viewBinding.includeListEmpty.emptyListContainer.isVisible = viewState.isEmpty()
        viewBinding.includeList.characterFavouriteListContainer.isVisible = viewState.isListed()
    }
}
