package com.stepango.blockme.feature.characters.favorite.api.di

import com.stepango.blockme.feature.characters.favorite.api.domain.repository.ICharacterFavoriteRepository
import com.stepango.blockme.feature.characters.favorite.api.domain.usecase.IDeleteCharacterFavoriteUseCase
import com.stepango.blockme.feature.characters.favorite.api.domain.usecase.IGetAllCharactersFavoriteUseCase
import com.stepango.blockme.feature.characters.favorite.api.domain.usecase.IGetCharacterFavoriteUseCase
import com.stepango.blockme.feature.characters.favorite.api.domain.usecase.ISetCharacterFavoriteUseCase

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