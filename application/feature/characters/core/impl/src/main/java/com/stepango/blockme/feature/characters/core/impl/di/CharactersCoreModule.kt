package com.stepango.blockme.feature.characters.core.impl.di

import com.stepango.blockme.common.util.clock.Clock
import com.stepango.blockme.common.util.mapper.Mapper
import com.stepango.blockme.core.network.library.Config
import com.stepango.blockme.core.network.library.response.BaseResponse
import com.stepango.blockme.feature.characters.core.api.data.response.CharacterResponse
import com.stepango.blockme.feature.characters.core.api.data.service.MarvelService
import com.stepango.blockme.feature.characters.core.api.domain.model.ICharacter
import com.stepango.blockme.feature.characters.core.api.domain.repository.MarvelRepository
import com.stepango.blockme.feature.characters.core.impl.data.mapper.CharacterMapper
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
    fun provideMarvelRepository(
        service: MarvelService,
        config: Config,
        clock: Clock,
        characterMapper: Mapper<BaseResponse<CharacterResponse>, List<ICharacter>>,
    ): MarvelRepository =
        ServiceMarvelRepository(service, config, clock, characterMapper)

    @Provides
    fun provideCharacterMapper(): Mapper<BaseResponse<CharacterResponse>, List<ICharacter>> = CharacterMapper()
}