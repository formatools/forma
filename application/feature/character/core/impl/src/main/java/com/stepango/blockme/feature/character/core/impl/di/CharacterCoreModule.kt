package com.stepango.blockme.feature.character.core.impl.di

import com.stepango.blockme.feature.character.core.api.domain.repository.MarvelRepository
import com.stepango.blockme.feature.character.core.api.data.service.MarvelService
import com.stepango.blockme.feature.character.core.impl.domain.repository.ServiceMarvelRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
internal class CharacterCoreModule {

    @Singleton
    @Provides
    fun provideMarvelService(retrofit: Retrofit): MarvelService = retrofit.create(MarvelService::class.java)

    @Singleton
    @Provides
    fun provideMarvelRepository(service: MarvelService): MarvelRepository = ServiceMarvelRepository(service)

}