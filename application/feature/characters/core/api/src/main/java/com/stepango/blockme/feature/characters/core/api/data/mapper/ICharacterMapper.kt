package com.stepango.blockme.feature.characters.core.api.data.mapper

import com.stepango.blockme.core.network.library.response.BaseResponse
import com.stepango.blockme.feature.characters.core.api.data.response.CharacterResponse
import com.stepango.blockme.feature.characters.core.api.domain.model.ICharacter

interface ICharacterMapper {

    @Throws(NoSuchElementException::class)
    suspend fun map(from: BaseResponse<CharacterResponse>): List<ICharacter>
}