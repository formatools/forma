package com.stepango.blockme.feature.characters.favorite.api.domain.usecase

// TODO: move to domain library
interface SetCharacterFavoriteUseCase {

    suspend operator fun invoke(id: Long, name: String, description: String, imageUrl: String)

}