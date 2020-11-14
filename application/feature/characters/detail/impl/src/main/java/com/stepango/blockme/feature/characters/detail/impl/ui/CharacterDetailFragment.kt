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

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.stepango.blockme.common.extensions.android.util.observe
import com.stepango.blockme.common.progressbar.databinding.ProgressBarDialog
import com.stepango.blockme.core.mvvm.library.ui.BaseFragment
import com.stepango.blockme.core.mvvm.library.viewModels
import com.stepango.blockme.feature.characters.core.api.di.CharactersCoreFeatureProvider
import com.stepango.blockme.feature.characters.detail.databinding.databinding.FragmentCharacterDetailBinding
import com.stepango.blockme.feature.characters.detail.impl.R
import com.stepango.blockme.feature.characters.detail.impl.di.DaggerCharacterDetailComponent

/**
 * View detail for selected character, displaying extra info and with option to add it to favorite.
 *
 * @see BaseFragment
 */
class CharacterDetailFragment :
    BaseFragment<FragmentCharacterDetailBinding>(
        layoutId = R.layout.fragment_character_detail
    ) {

    private val viewModel: CharacterDetailViewModel by viewModels()

    private val args: CharacterDetailFragmentArgs by navArgs()

    private lateinit var progressDialog: ProgressBarDialog

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param view The view returned by onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     * @see BaseFragment.onViewCreated
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressBarDialog(requireCompatActivity())

        observe(viewModel.state, ::onViewStateChange)
        viewModel.loadCharacterDetail(args.characterId)
    }

    /**
     * Initialize dagger injection dependency graph.
     */
    override fun onInitDependencyInjection() {
        DaggerCharacterDetailComponent
            .factory()
            .create(
                requireProvider(CharactersCoreFeatureProvider::class).getCharactersCoreFeature()
            )
            .inject(this)
    }

    /**
     * Initialize view data binding variables.
     */
    override fun onInitDataBinding() {
        viewBinding.viewModel = viewModel
    }

    // ============================================================================================
    //  Private observers methods
    // ============================================================================================

    /**
     * Observer view state change on [CharacterDetailViewState].
     *
     * @param viewState State of character detail.
     */
    private fun onViewStateChange(viewState: ICharacterDetailViewState) {
        when (viewState) {
            is com.stepango.blockme.feature.characters.detail.impl.ui.CharacterDetailViewState.Loading ->
                progressDialog.show(R.string.character_detail_dialog_loading_text)
            is com.stepango.blockme.feature.characters.detail.impl.ui.CharacterDetailViewState.Error ->
                progressDialog.dismissWithErrorMessage(R.string.character_detail_dialog_error_text)
            is com.stepango.blockme.feature.characters.detail.impl.ui.CharacterDetailViewState.AddedToFavorite ->
                Snackbar.make(
                    requireView(),
                    R.string.character_detail_added_to_favorite_message,
                    Snackbar.LENGTH_LONG
                ).show()
            is com.stepango.blockme.feature.characters.detail.impl.ui.CharacterDetailViewState.Dismiss ->
                findNavController().navigateUp()
            else -> progressDialog.dismiss()
        }
    }
}
