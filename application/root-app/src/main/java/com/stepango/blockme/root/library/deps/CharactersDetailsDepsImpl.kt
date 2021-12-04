package com.stepango.blockme.root.library.deps

import com.stepango.blockme.feature.characters.detail.api.CharacterDetailsDeps
import com.stepango.blockme.feature.characters.favorite.api.domain.usecase.GetCharacterFavoriteUseCase
import com.stepango.blockme.feature.characters.favorite.api.domain.usecase.SetCharacterFavoriteUseCase
import com.stepango.blockme.feature.characters.favorite.api.CharactersFavoriteApi
import javax.inject.Inject

class CharactersDetailsDepsImpl @Inject constructor(
    private val charactersFavoriteApi: CharactersFavoriteApi
) : CharacterDetailsDeps {
    override val setCharacterFavoriteUseCase: SetCharacterFavoriteUseCase
        get() = charactersFavoriteApi.setCharacterFavoriteUseCase
    override val getCharacterFavoriteUseCase: GetCharacterFavoriteUseCase
        get() = charactersFavoriteApi.getCharacterFavoriteUseCase
}