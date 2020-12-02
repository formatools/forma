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

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import com.stepango.blockme.common.extensions.android.util.gridLayoutManager
import com.stepango.blockme.common.extensions.android.util.observe
import com.stepango.blockme.core.mvvm.library.ui.BaseFragment
import com.stepango.blockme.core.mvvm.library.viewModels
import com.stepango.blockme.feature.characters.core.api.di.CharactersCoreFeatureProvider
import com.stepango.blockme.feature.characters.list.databinding.databinding.FragmentCharactersListBinding
import com.stepango.blockme.feature.characters.list.impl.R
import com.stepango.blockme.feature.characters.list.impl.di.DaggerCharactersListComponent
import com.stepango.blockme.feature.characters.list.impl.domain.model.ICharacterItem
import com.stepango.blockme.feature.characters.list.impl.domain.model.ICharactersListViewEvent
import com.stepango.blockme.feature.characters.list.impl.domain.model.ICharactersListViewState
import com.stepango.blockme.feature.characters.list.impl.ui.adapter.CharactersListAdapter
import com.stepango.blockme.feature.characters.list.impl.ui.adapter.CharactersListAdapterState


class CharactersListFragment :
    BaseFragment<FragmentCharactersListBinding>(
        layoutId = R.layout.fragment_characters_list
    ) {

    private val viewModel: CharactersListViewModel by viewModels()

    private lateinit var viewAdapter: CharactersListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    override fun onInitDataBinding() {
        viewAdapter = CharactersListAdapter(viewModel)

        viewBinding.viewModel = viewModel
        viewBinding.includeList.charactersList.apply {
            adapter = viewAdapter
            gridLayoutManager?.spanSizeLookup = viewAdapter.getSpanSizeLookup()
        }
    }

    private fun onViewDataChange(viewData: PagedList<ICharacterItem>) {
        viewAdapter.submitList(viewData)
    }

    private fun onViewStateChange(viewState: ICharactersListViewState) {
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
                        .actionCharactersListFragmentToCharacterDetailFragment(viewEvent.id))
        }
    }
}
