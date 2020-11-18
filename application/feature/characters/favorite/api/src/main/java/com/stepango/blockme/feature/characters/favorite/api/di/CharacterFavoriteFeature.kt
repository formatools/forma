package com.stepango.blockme.feature.characters.favorite.api.di

import com.stepango.blockme.feature.characters.favorite.api.domain.repository.ICharacterFavoriteRepository

interface CharacterFavoriteFeature {

    fun getCharacterFavoriteRepository(): ICharacterFavoriteRepository
}

interface CharacterFavoriteFeatureProvider {

    fun getCharacterFavoriteFeature(): CharacterFavoriteFeature
}