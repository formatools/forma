package com.stepango.blockme.feature.characters.detail.api

import com.stepango.blockme.feature.characters.favorite.api.domain.usecase.GetCharacterFavoriteUseCase
import com.stepango.blockme.feature.characters.favorite.api.domain.usecase.SetCharacterFavoriteUseCase
import kotlin.properties.Delegates

interface CharacterDetailsDeps {

    val setCharacterFavoriteUseCase: SetCharacterFavoriteUseCase
    val getCharacterFavoriteUseCase: GetCharacterFavoriteUseCase

}

interface CharacterDetailsDepsProvider {

    val deps: CharacterDetailsDeps

    companion object : CharacterDetailsDepsProvider by CharacterDetailsDepsStore

}

object CharacterDetailsDepsStore : CharacterDetailsDepsProvider {

    override var deps: CharacterDetailsDeps by Delegates.notNull()

}