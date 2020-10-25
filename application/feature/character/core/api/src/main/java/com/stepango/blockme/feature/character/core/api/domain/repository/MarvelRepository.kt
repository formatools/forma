package com.stepango.blockme.feature.character.core.api.domain.repository

import com.stepango.blockme.core.network.library.response.BaseResponse
import com.stepango.blockme.feature.character.core.api.data.response.CharacterResponse

interface MarvelRepository {

    suspend fun getCharacter(id: Long): BaseResponse<CharacterResponse>

    suspend fun getCharacters(offset: Int, limit: Int): BaseResponse<CharacterResponse>
}