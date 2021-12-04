package com.stepango.blockme.feature.characters.favorite.api

import androidx.fragment.app.Fragment
import com.stepango.blockme.feature.characters.favorite.api.domain.usecase.GetCharacterFavoriteUseCase
import com.stepango.blockme.feature.characters.favorite.api.domain.usecase.SetCharacterFavoriteUseCase

interface CharactersFavoriteApi {

    val getCharacterFavoriteUseCase: GetCharacterFavoriteUseCase
    val setCharacterFavoriteUseCase: SetCharacterFavoriteUseCase

    // TODO: find the way to integrate this with google navigation
    fun getFragment(): Fragment

}