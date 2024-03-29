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

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import by.kirich1409.viewbindingdelegate.viewBinding
import tools.forma.sample.common.extensions.android.util.gridLayoutManager
import tools.forma.sample.common.extensions.android.util.observe
import tools.forma.sample.common.recyclerview.widget.RecyclerViewItemDecoration
import tools.forma.sample.core.mvvm.library.ui.BaseViewBindingFragment
import tools.forma.sample.core.mvvm.library.viewModels
import tools.forma.sample.feature.characters.core.api.di.CharactersCoreFeatureProvider
import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter
import tools.forma.sample.feature.characters.list.viewbinding.databinding.FragmentCharactersListBinding
import tools.forma.sample.feature.characters.list.impl.R
import tools.forma.sample.feature.characters.list.impl.di.DaggerCharactersListComponent
import tools.forma.sample.feature.characters.list.impl.domain.model.ICharactersListViewEvent
import tools.forma.sample.feature.characters.list.impl.domain.model.ICharactersListViewState
import tools.forma.sample.feature.characters.list.impl.ui.adapter.CharactersListAdapter
import tools.forma.sample.feature.characters.list.impl.ui.adapter.CharactersListAdapterState

class CharactersListFragment : BaseViewBindingFragment(
    layoutId = tools.forma.sample.feature.characters.list.viewbinding.R.layout.fragment_characters_list
) {

    private val viewModel: CharactersListViewModel by viewModels()
    private val viewBinding: FragmentCharactersListBinding by viewBinding(FragmentCharactersListBinding::bind)

    private lateinit var viewAdapter: CharactersListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observe(viewModel.state, ::onViewStateChange)
        observe(viewModel.data, ::onViewDataChange)
        observe(viewModel.event, ::onViewEvent)
    }

    override fun onInitDependencyInjection() {
        DaggerCharactersListComponent
            .factory()
            .create(
                requireProvider(CharactersCoreFeatureProvider::class).getCharactersCoreFeature()
            )
            .inject(this)
    }

    private fun setupViews() {
        viewBinding.swipeRefresh.setOnRefreshListener { viewModel.refreshLoadedCharactersList() }

        viewAdapter = CharactersListAdapter(viewModel)
        viewBinding.includeList.charactersList.apply {
            addItemDecoration(
                RecyclerViewItemDecoration(
                    resources.getDimensionPixelSize(tools.forma.sample.feature.character.list.res.R.dimen.characters_list_item_padding)
                )
            )
            adapter = viewAdapter
            gridLayoutManager?.spanSizeLookup = viewAdapter.getSpanSizeLookup()
        }
    }

    private fun onViewDataChange(viewData: PagedList<ICharacter>) {
        viewAdapter.submitList(viewData)
    }

    private fun onViewStateChange(viewState: ICharactersListViewState) {
        viewBinding.swipeRefresh.isRefreshing = viewState.isRefreshing()

        viewBinding.includeList.charactersListContainer.isVisible =
            viewState.isLoaded() or viewState.isAddLoading() or viewState.isAddError() or viewState.isNoMoreElements()
        viewBinding.includeListEmpty.emptyContainer.isVisible = viewState.isEmpty()
        viewBinding.includeListError.errorContainer.isVisible = viewState.isError()
        viewBinding.includeListLoading.loadingContainer.isVisible = viewState.isLoading()

        when (viewState) {
            is CharactersListViewState.Loaded ->
                viewAdapter.submitState(CharactersListAdapterState.Added)
            is CharactersListViewState.AddLoading ->
                viewAdapter.submitState(CharactersListAdapterState.AddLoading)
            is CharactersListViewState.AddError ->
                viewAdapter.submitState(CharactersListAdapterState.AddError)
            is CharactersListViewState.NoMoreElements ->
                viewAdapter.submitState(CharactersListAdapterState.NoMore)
        }
    }

    private fun onViewEvent(viewEvent: ICharactersListViewEvent) {
        when (viewEvent) {
            // TODO https://github.com/formatools/forma/issues/46
            // Need abstract navigation layer here
            is CharactersListViewEvent.OpenCharacterDetail ->
                findNavController().navigate(
                    CharactersListFragmentDirections
                        .actionCharactersListFragmentToCharacterDetailFragment(viewEvent.id)
                )
        }
    }
}
