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

package com.stepango.blockme.feature.characters.favorite.impl.data.repository

import com.stepango.blockme.feature.characters.favorite.api.domain.model.ICharacterFavorite
import com.stepango.blockme.feature.characters.favorite.api.domain.repository.ICharacterFavoriteRepository
import com.stepango.blockme.feature.characters.favorite.impl.data.database.CharacterFavoriteDao
import com.stepango.blockme.feature.characters.favorite.impl.data.model.CharacterFavorite
import javax.inject.Inject

class CharacterFavoriteRepository @Inject constructor(
    private val characterFavoriteDao: CharacterFavoriteDao
) : ICharacterFavoriteRepository {

    override suspend fun getAllCharactersFavorite(): List<ICharacterFavorite> =
        characterFavoriteDao.getAllCharactersFavorite()

    override suspend fun getCharacterFavorite(characterFavoriteId: Long): ICharacterFavorite? =
        characterFavoriteDao.getCharacterFavorite(characterFavoriteId)

    override suspend fun deleteAllCharactersFavorite() =
        characterFavoriteDao.deleteAllCharactersFavorite()

    override suspend fun deleteCharacterFavoriteById(characterFavoriteId: Long) =
        characterFavoriteDao.deleteCharacterFavoriteById(characterFavoriteId)

    override suspend fun deleteCharacterFavorite(character: ICharacterFavorite) =
        characterFavoriteDao.deleteCharacterFavorite(character as CharacterFavorite)

    override suspend fun insertCharactersFavorites(characters: List<ICharacterFavorite>) =
        characterFavoriteDao.insertCharactersFavorites(characters as List<CharacterFavorite>)

    override suspend fun insertCharacterFavorite(id: Long, name: String, imageUrl: String) {
        val characterFavorite = CharacterFavorite(
            id = id,
            name = name,
            imageUrl = imageUrl
        )
        characterFavoriteDao.insertCharacterFavorite(characterFavorite)
    }
}