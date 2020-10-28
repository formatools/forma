package com.stepango.blockme.feature.characters.core.impl.di

import com.stepango.blockme.feature.characters.core.api.domain.repository.MarvelRepository
import com.stepango.blockme.feature.characters.core.api.data.service.MarvelService
import com.stepango.blockme.feature.characters.core.impl.domain.repository.ServiceMarvelRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
internal class CharactersCoreModule {

    @Singleton
    @Provides
    fun provideMarvelService(retrofit: Retrofit): MarvelService = retrofit.create(MarvelService::class.java)

    @Singleton
    @Provides
    fun provideMarvelRepository(service: MarvelService): MarvelRepository = ServiceMarvelRepository(service)

}