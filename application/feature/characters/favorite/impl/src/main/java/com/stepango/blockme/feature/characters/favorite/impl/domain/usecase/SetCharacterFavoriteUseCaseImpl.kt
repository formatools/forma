package com.stepango.blockme.feature.characters.favorite.impl.domain.usecase

import com.stepango.blockme.feature.characters.favorite.api.domain.usecase.SetCharacterFavoriteUseCase
import com.stepango.blockme.feature.characters.favorite.impl.data.repository.CharacterFavoriteRepository
import javax.inject.Inject

internal class SetCharacterFavoriteUseCaseImpl @Inject constructor(
    private val repository: CharacterFavoriteRepository
) : SetCharacterFavoriteUseCase {

    override suspend operator fun invoke(id: Long, name: String, description: String, imageUrl: String) {
        repository.insertCharacterFavorite(id, name, description, imageUrl)
    }

}