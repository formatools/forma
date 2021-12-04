package com.stepango.blockme.feature.characters.favorite.impl.domain.usecase

import com.stepango.blockme.feature.characters.core.api.domain.model.ICharacter
import com.stepango.blockme.feature.characters.favorite.impl.data.repository.CharacterFavoriteRepository
import javax.inject.Inject

internal class DeleteCharacterFavoriteUseCase @Inject constructor(private val repository: CharacterFavoriteRepository) {

    suspend operator fun invoke(character: ICharacter) {
        repository.deleteCharacterFavorite(character)
    }
}