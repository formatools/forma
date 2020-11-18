package com.stepango.blockme.feature.characters.favorite.api.domain.repository

import com.stepango.blockme.feature.characters.favorite.api.domain.model.ICharacterFavorite

interface ICharacterFavoriteRepository {

    suspend fun getAllCharactersFavorite(): List<ICharacterFavorite>

    suspend fun getCharacterFavorite(characterFavoriteId: Long): ICharacterFavorite?

    suspend fun deleteAllCharactersFavorite()

    suspend fun deleteCharacterFavoriteById(characterFavoriteId: Long)

    suspend fun deleteCharacterFavorite(character: ICharacterFavorite)

    suspend fun insertCharactersFavorites(characters: List<ICharacterFavorite>)

    suspend fun insertCharacterFavorite(id: Long, name: String, imageUrl: String)
}
