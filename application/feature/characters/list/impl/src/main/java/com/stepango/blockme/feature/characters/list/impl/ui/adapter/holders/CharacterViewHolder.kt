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
import androidx.recyclerview.widget.RecyclerView
import com.stepango.blockme.common.extensions.android.util.loadImage
import com.stepango.blockme.core.mvvm.library.ui.BaseViewHolder
import com.stepango.blockme.feature.characters.list.viewbinding.databinding.ListItemCharacterBinding
import com.stepango.blockme.feature.characters.core.api.domain.model.ICharacter

class CharacterViewHolder(
    inflater: LayoutInflater,
    private val itemClickListener: (Int) -> Unit
) : BaseViewHolder<ListItemCharacterBinding>(
    binding = ListItemCharacterBinding.inflate(inflater)
) {

    init {
        binding.root.setOnClickListener {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                itemClickListener(position)
            }
        }
    }

    fun bind(item: ICharacter) {
        binding.characterImage.loadImage(item.imageUrl)
        binding.characterName.text = item.name
    }
}
