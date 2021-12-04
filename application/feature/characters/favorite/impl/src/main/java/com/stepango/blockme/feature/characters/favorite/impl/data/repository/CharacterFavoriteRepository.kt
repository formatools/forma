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

import com.stepango.blockme.feature.characters.core.api.domain.model.ICharacter
import com.stepango.blockme.feature.characters.favorite.impl.data.database.CharacterFavoriteDao
import com.stepango.blockme.feature.characters.favorite.impl.data.model.CharacterFavorite
import javax.inject.Inject

internal class CharacterFavoriteRepository @Inject constructor(
    private val characterFavoriteDao: CharacterFavoriteDao
) {

    suspend fun getAllCharactersFavorite(): List<ICharacter> =
        characterFavoriteDao.getAllCharactersFavorite()

    suspend fun getCharacterFavorite(characterFavoriteId: Long): ICharacter? =
        characterFavoriteDao.getCharacterFavorite(characterFavoriteId)

    suspend fun deleteAllCharactersFavorite() =
        characterFavoriteDao.deleteAllCharactersFavorite()

    suspend fun deleteCharacterFavoriteById(characterFavoriteId: Long) =
        characterFavoriteDao.deleteCharacterFavoriteById(characterFavoriteId)

    suspend fun deleteCharacterFavorite(character: ICharacter) =
        characterFavoriteDao.deleteCharacterFavorite(character as CharacterFavorite)

    suspend fun insertCharactersFavorites(characters: List<ICharacter>) =
        characterFavoriteDao.insertCharactersFavorites(characters as List<CharacterFavorite>)

    suspend fun insertCharacterFavorite(
        id: Long,
        name: String,
        description: String,
        imageUrl: String,
    ) {
        val characterFavorite = CharacterFavorite(
            id = id,
            name = name,
            description = description,
            imageUrl = imageUrl
        )
        characterFavoriteDao.insertCharacterFavorite(characterFavorite)
    }
}