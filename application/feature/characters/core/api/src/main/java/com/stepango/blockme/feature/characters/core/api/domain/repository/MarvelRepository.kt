package com.stepango.blockme.feature.characters.core.api.domain.repository

import com.stepango.blockme.feature.characters.core.api.domain.model.ICharacter

interface MarvelRepository {

    suspend fun getCharacter(id: Long): ICharacter

    suspend fun getCharacters(offset: Int, limit: Int): List<ICharacter>
}