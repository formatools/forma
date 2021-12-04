package com.stepango.blockme.feature.characters.favorite.impl.domain.usecase

import com.stepango.blockme.feature.characters.core.api.domain.model.ICharacter
import com.stepango.blockme.feature.characters.favorite.api.domain.usecase.GetCharacterFavoriteUseCase
import com.stepango.blockme.feature.characters.favorite.impl.data.repository.CharacterFavoriteRepository
import javax.inject.Inject

internal class GetCharacterFavoriteUseCaseImpl @Inject constructor(
    private val repository: CharacterFavoriteRepository
) : GetCharacterFavoriteUseCase {

    override suspend operator fun invoke(characterFavoriteId: Long): ICharacter? =
        repository.getCharacterFavorite(characterFavoriteId)
}