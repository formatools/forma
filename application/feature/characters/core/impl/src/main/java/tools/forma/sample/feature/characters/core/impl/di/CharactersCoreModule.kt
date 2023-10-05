package tools.forma.sample.feature.characters.core.impl.di

import tools.forma.sample.common.util.clock.Clock
import tools.forma.sample.common.util.mapper.Mapper
import tools.forma.sample.core.network.library.Config
import tools.forma.sample.core.network.library.response.BaseResponse
import tools.forma.sample.feature.characters.core.api.data.response.CharacterResponse
import tools.forma.sample.feature.characters.core.api.data.service.MarvelService
import tools.forma.sample.feature.characters.core.api.domain.model.ICharacter
import tools.forma.sample.feature.characters.core.api.domain.repository.MarvelRepository
import tools.forma.sample.feature.characters.core.impl.data.mapper.CharacterMapper
import tools.forma.sample.feature.characters.core.impl.domain.repository.ServiceMarvelRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
internal class CharactersCoreModule {

    @Singleton
    @Provides
    fun provideMarvelService(retrofit: Retrofit): MarvelService =
        retrofit.create(MarvelService::class.java)

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
    fun provideCharacterMapper(): Mapper<BaseResponse<CharacterResponse>, List<ICharacter>> =
        CharacterMapper()
}