package com.stepango.blockme.feature.characters.favorite.api.domain.usecase

import com.stepango.blockme.feature.characters.favorite.api.domain.model.ICharacterFavorite

interface IGetCharacterFavoriteUseCase {

    suspend operator fun invoke(characterFavoriteId: Long): ICharacterFavorite?
}