package com.stepango.blockme.feature.characters.core.api.di

import com.stepango.blockme.feature.characters.core.api.domain.repository.MarvelRepository

interface CharactersCoreApi {

    fun getMarvelRepository(): MarvelRepository

}