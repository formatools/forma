package tools.forma.sample.feature.characters.favorite.api.di

import tools.forma.sample.feature.characters.favorite.api.domain.repository.ICharacterFavoriteRepository
import tools.forma.sample.feature.characters.favorite.api.domain.usecase.IDeleteCharacterFavoriteUseCase
import tools.forma.sample.feature.characters.favorite.api.domain.usecase.IGetAllCharactersFavoriteUseCase
import tools.forma.sample.feature.characters.favorite.api.domain.usecase.IGetCharacterFavoriteUseCase
import tools.forma.sample.feature.characters.favorite.api.domain.usecase.ISetCharacterFavoriteUseCase

interface CharacterFavoriteFeature {

    fun getCharacterFavoriteRepository(): ICharacterFavoriteRepository

    fun getCharacterFavoriteUseCase(): IGetCharacterFavoriteUseCase

    fun getSetCharacterFavoriteUseCase(): ISetCharacterFavoriteUseCase

    fun getAllCharactersFavoriteUseCase(): IGetAllCharactersFavoriteUseCase

    fun getDeleteCharacterFavoriteUseCase(): IDeleteCharacterFavoriteUseCase
}

interface CharacterFavoriteFeatureProvider {

    fun getCharacterFavoriteFeature(): CharacterFavoriteFeature
}