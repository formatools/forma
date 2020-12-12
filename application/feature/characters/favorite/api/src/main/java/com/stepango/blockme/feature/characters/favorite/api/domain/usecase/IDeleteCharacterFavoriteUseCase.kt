package com.stepango.blockme.feature.characters.favorite.api.domain.usecase

import com.stepango.blockme.feature.characters.favorite.api.domain.model.ICharacterFavorite

interface IDeleteCharacterFavoriteUseCase {

    suspend operator fun invoke(character: ICharacterFavorite)
}