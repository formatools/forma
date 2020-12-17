package com.stepango.blockme.feature.characters.favorite.impl.domain.usecase

import com.stepango.blockme.feature.characters.favorite.api.domain.model.ICharacterFavorite
import com.stepango.blockme.feature.characters.favorite.api.domain.repository.ICharacterFavoriteRepository
import com.stepango.blockme.feature.characters.favorite.api.domain.usecase.IDeleteCharacterFavoriteUseCase
import javax.inject.Inject

class DeleteCharacterFavoriteUseCase @Inject constructor(private val repository: ICharacterFavoriteRepository) : IDeleteCharacterFavoriteUseCase {

    override suspend operator fun invoke(character: ICharacterFavorite) {
        repository.deleteCharacterFavorite(character)
    }
}