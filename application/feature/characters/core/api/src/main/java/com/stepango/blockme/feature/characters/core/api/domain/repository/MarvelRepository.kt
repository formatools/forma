package com.stepango.blockme.feature.characters.core.api.domain.repository

import com.stepango.blockme.core.network.library.response.BaseResponse
import com.stepango.blockme.feature.characters.core.api.data.response.CharacterResponse

interface MarvelRepository {

    suspend fun getCharacter(id: Long): BaseResponse<CharacterResponse>

    suspend fun getCharacters(offset: Int, limit: Int): BaseResponse<CharacterResponse>
}