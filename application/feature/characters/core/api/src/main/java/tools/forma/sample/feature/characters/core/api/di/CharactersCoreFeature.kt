package tools.forma.sample.feature.characters.core.api.di

import tools.forma.sample.feature.characters.core.api.domain.repository.MarvelRepository
import tools.forma.sample.feature.characters.core.api.domain.usecase.IGetCharacterUseCase
import tools.forma.sample.feature.characters.core.api.domain.usecase.IGetCharactersUseCase

interface CharactersCoreFeature {

    fun getMarvelRepository(): MarvelRepository

    fun getCharactersUseCase(): IGetCharactersUseCase

    fun getCharacterUseCase(): IGetCharacterUseCase
}
