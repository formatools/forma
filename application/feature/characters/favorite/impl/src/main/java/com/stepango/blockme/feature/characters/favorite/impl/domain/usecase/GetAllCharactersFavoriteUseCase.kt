package com.stepango.blockme.feature.characters.favorite.impl.domain.usecase

import com.stepango.blockme.feature.characters.core.api.domain.model.ICharacter
import com.stepango.blockme.feature.characters.favorite.api.domain.repository.ICharacterFavoriteRepository
import com.stepango.blockme.feature.characters.favorite.api.domain.usecase.IGetAllCharactersFavoriteUseCase
import javax.inject.Inject

class GetAllCharactersFavoriteUseCase @Inject constructor(private val repository: ICharacterFavoriteRepository) : IGetAllCharactersFavoriteUseCase {

    override suspend operator fun invoke(): List<ICharacter> =
        repository.getAllCharactersFavorite()
}