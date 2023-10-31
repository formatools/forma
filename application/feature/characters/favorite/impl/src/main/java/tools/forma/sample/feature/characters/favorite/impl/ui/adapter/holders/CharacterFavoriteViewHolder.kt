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

package tools.forma.sample.feature.characters.favorite.impl.ui.adapter.holders

import android.view.LayoutInflater
import tools.forma.sample.common.extensions.android.util.loadImage
import tools.forma.sample.core.mvvm.library.ui.BaseViewHolder
import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter
import tools.forma.sample.feature.favorite.viewbinding.databinding.ListItemCharacterFavoriteBinding

class CharacterFavoriteViewHolder(
    inflater: LayoutInflater
) : BaseViewHolder<ListItemCharacterFavoriteBinding>(
    binding = ListItemCharacterFavoriteBinding.inflate(inflater)
) {

    fun bind(characterFavorite: ICharacter) {
        binding.characterImage.loadImage(characterFavorite.imageUrl)
        binding.characterName.text = characterFavorite.name
    }
}