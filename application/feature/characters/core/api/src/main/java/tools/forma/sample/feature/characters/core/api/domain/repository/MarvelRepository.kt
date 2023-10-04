package tools.forma.sample.feature.characters.core.api.domain.repository

import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter

interface MarvelRepository {

    suspend fun getCharacter(id: Long): ICharacter

    suspend fun getCharacters(offset: Int, limit: Int): List<ICharacter>
}