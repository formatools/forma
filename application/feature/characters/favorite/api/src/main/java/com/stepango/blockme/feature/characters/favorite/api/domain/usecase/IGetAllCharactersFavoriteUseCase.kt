package com.stepango.blockme.feature.characters.favorite.api.domain.usecase

import com.stepango.blockme.feature.characters.favorite.api.domain.model.ICharacterFavorite

interface IGetAllCharactersFavoriteUseCase {

    suspend operator fun invoke(): List<ICharacterFavorite>
}