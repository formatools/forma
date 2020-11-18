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

package com.stepango.blockme.feature.characters.favorite.impl.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stepango.blockme.core.mvvm.library.ui.BaseListAdapter
import com.stepango.blockme.feature.characters.favorite.api.domain.model.ICharacterFavorite
import com.stepango.blockme.feature.characters.favorite.impl.ui.adapter.holders.CharacterFavoriteViewHolder

class CharacterFavoriteAdapter : BaseListAdapter<ICharacterFavorite>(
    itemsSame = { old, new -> old.id == new.id },
    contentsSame = { old, new -> old == new }
) {

    override fun onCreateViewHolder(parent: ViewGroup, inflater: LayoutInflater, viewType: Int) =
        CharacterFavoriteViewHolder(inflater)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CharacterFavoriteViewHolder -> holder.bind(getItem(position))
        }
    }
}
