package com.stepango.blockme.feature.characters.favorite.impl.domain.usecase

import com.stepango.blockme.feature.characters.core.api.domain.model.ICharacter
import com.stepango.blockme.feature.characters.favorite.api.domain.repository.ICharacterFavoriteRepository
import com.stepango.blockme.feature.characters.favorite.api.domain.usecase.IGetCharacterFavoriteUseCase
import javax.inject.Inject

class GetCharacterFavoriteUseCase @Inject constructor(private val repository: ICharacterFavoriteRepository) : IGetCharacterFavoriteUseCase {

    override suspend operator fun invoke(characterFavoriteId: Long): ICharacter? =
        repository.getCharacterFavorite(characterFavoriteId)
}