package tools.forma.sample.feature.characters.core.api.di

import tools.forma.sample.feature.characters.core.api.domain.repository.MarvelRepository

interface CharactersCoreFeature {

    fun getMarvelRepository(): MarvelRepository
}