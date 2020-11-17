package com.stepango.blockme.feature.characters.favorite.impl.domain.repository

import androidx.lifecycle.LiveData

import com.stepango.blockme.feature.characters.favorite.impl.domain.entity.CharacterFavorite

interface CharacterFavoriteRepository {

    fun getAllCharactersFavoriteLiveData(): LiveData<List<CharacterFavorite>>

    suspend fun getAllCharactersFavorite(): List<CharacterFavorite>

    suspend fun getCharacterFavorite(characterFavoriteId: Long): CharacterFavorite?

    suspend fun deleteAllCharactersFavorite()

    suspend fun deleteCharacterFavoriteById(characterFavoriteId: Long)

    suspend fun deleteCharacterFavorite(character: CharacterFavorite)

    suspend fun insertCharactersFavorites(characters: List<CharacterFavorite>)

    suspend fun insertCharacterFavorite(id: Long, name: String, imageUrl: String)
}
