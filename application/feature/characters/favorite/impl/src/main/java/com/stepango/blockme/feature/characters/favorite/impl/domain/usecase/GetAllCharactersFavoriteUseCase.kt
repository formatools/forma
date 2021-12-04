package com.stepango.blockme.feature.characters.favorite.impl.domain.usecase

import com.stepango.blockme.feature.characters.core.api.domain.model.ICharacter
import com.stepango.blockme.feature.characters.favorite.impl.data.repository.CharacterFavoriteRepository
import javax.inject.Inject

internal class GetAllCharactersFavoriteUseCase @Inject constructor(
    private val repository: CharacterFavoriteRepository
) {

    suspend operator fun invoke(): List<ICharacter> =
        repository.getAllCharactersFavorite()

}