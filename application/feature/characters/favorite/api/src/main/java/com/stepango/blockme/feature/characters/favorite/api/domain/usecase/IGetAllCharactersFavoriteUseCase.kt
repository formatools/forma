package com.stepango.blockme.feature.characters.favorite.api.domain.usecase

import com.stepango.blockme.feature.characters.core.api.domain.model.ICharacter

interface IGetAllCharactersFavoriteUseCase {

    suspend operator fun invoke(): List<ICharacter>
}