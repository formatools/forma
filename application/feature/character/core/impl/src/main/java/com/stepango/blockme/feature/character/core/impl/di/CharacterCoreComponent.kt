package com.stepango.blockme.feature.character.core.impl.di

import com.stepango.blockme.core.network.library.di.NetworkModule
import com.stepango.blockme.feature.character.core.api.domain.repository.MarvelRepository
import com.stepango.blockme.feature.character.core.api.data.service.MarvelService
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    CharacterCoreModule::class,
    NetworkModule::class
])
internal interface CharacterCoreComponent {

    fun marvelService(): MarvelService

    fun marvelRepository(): MarvelRepository
}
