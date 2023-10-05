package tools.forma.sample.feature.characters.favorite.api.domain.repository

import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter

interface ICharacterFavoriteRepository {

    suspend fun getAllCharactersFavorite(): List<ICharacter>

    suspend fun getCharacterFavorite(characterFavoriteId: Long): ICharacter?

    suspend fun deleteAllCharactersFavorite()

    suspend fun deleteCharacterFavoriteById(characterFavoriteId: Long)

    suspend fun deleteCharacterFavorite(character: ICharacter)

    suspend fun insertCharactersFavorites(characters: List<ICharacter>)

    suspend fun insertCharacterFavorite(id: Long, name: String, description: String, imageUrl: String)
}
