package com.stepango.blockme.feature.characters.favorite.impl.domain.usecase

import com.stepango.blockme.feature.characters.favorite.api.domain.repository.ICharacterFavoriteRepository
import com.stepango.blockme.feature.characters.favorite.api.domain.usecase.ISetCharacterFavoriteUseCase
import javax.inject.Inject

class SetCharacterFavoriteUseCase @Inject constructor(private val repository: ICharacterFavoriteRepository) : ISetCharacterFavoriteUseCase {

    override suspend operator fun invoke(id: Long, name: String, imageUrl: String) {
        repository.insertCharacterFavorite(id, name, imageUrl)
    }
}