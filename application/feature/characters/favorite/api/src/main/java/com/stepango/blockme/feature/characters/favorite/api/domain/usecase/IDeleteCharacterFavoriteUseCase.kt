package com.stepango.blockme.feature.characters.favorite.api.domain.usecase

import com.stepango.blockme.feature.characters.core.api.domain.model.ICharacter

interface IDeleteCharacterFavoriteUseCase {

    suspend operator fun invoke(character: ICharacter)
}