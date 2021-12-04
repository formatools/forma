package com.stepango.blockme.feature.characters.favorite.api.domain.usecase

import com.stepango.blockme.feature.characters.core.api.domain.model.ICharacter

// TODO: move to domain library
interface GetCharacterFavoriteUseCase {

    suspend operator fun invoke(characterFavoriteId: Long): ICharacter?

}