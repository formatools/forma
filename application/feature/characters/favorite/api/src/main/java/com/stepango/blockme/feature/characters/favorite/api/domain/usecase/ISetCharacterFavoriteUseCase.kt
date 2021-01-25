package com.stepango.blockme.feature.characters.favorite.api.domain.usecase

interface ISetCharacterFavoriteUseCase {

    suspend operator fun invoke(id: Long, name: String, description: String, imageUrl: String)
}