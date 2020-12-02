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

package com.stepango.blockme.feature.characters.list.impl.ui.adapter.holders

import android.view.LayoutInflater
import com.stepango.blockme.core.mvvm.library.ui.BaseViewHolder
import com.stepango.blockme.feature.characters.list.databinding.databinding.ListItemCharacterBinding
import com.stepango.blockme.feature.characters.list.impl.domain.model.ICharacterItem
import com.stepango.blockme.feature.characters.list.impl.ui.CharactersListViewModel

class CharacterViewHolder(
    inflater: LayoutInflater
) : BaseViewHolder<ListItemCharacterBinding>(
    binding = ListItemCharacterBinding.inflate(inflater)
) {

    fun bind(viewModel: CharactersListViewModel, item: ICharacterItem) {
        binding.viewModel = viewModel
        binding.character = item
        binding.executePendingBindings()
    }
}
