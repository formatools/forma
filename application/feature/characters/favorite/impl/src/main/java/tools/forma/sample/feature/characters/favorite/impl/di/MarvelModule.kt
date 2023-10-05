/*
 * Copyright 2019 vmadalin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tools.forma.sample.feature.characters.favorite.impl.di

import android.content.Context
import androidx.room.Room
import tools.forma.sample.feature.characters.favorite.api.domain.repository.ICharacterFavoriteRepository
import tools.forma.sample.feature.characters.favorite.api.domain.usecase.IDeleteCharacterFavoriteUseCase
import tools.forma.sample.feature.characters.favorite.api.domain.usecase.IGetAllCharactersFavoriteUseCase
import tools.forma.sample.feature.characters.favorite.api.domain.usecase.IGetCharacterFavoriteUseCase
import tools.forma.sample.feature.characters.favorite.api.domain.usecase.ISetCharacterFavoriteUseCase
import tools.forma.sample.feature.characters.favorite.impl.data.database.MarvelDatabase
import tools.forma.sample.feature.characters.favorite.impl.data.database.migrations.MIGRATION_1_2
import tools.forma.sample.feature.characters.favorite.impl.data.repository.CharacterFavoriteRepository
import tools.forma.sample.feature.characters.favorite.impl.domain.usecase.DeleteCharacterFavoriteUseCase
import tools.forma.sample.feature.characters.favorite.impl.domain.usecase.GetAllCharactersFavoriteUseCase
import tools.forma.sample.feature.characters.favorite.impl.domain.usecase.GetCharacterFavoriteUseCase
import tools.forma.sample.feature.characters.favorite.impl.domain.usecase.SetCharacterFavoriteUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal abstract class MarvelModule {

    @Singleton
    @Binds
    abstract fun bindsCharacterFavoriteRepository(
        repository: CharacterFavoriteRepository
    ): ICharacterFavoriteRepository

    @Binds
    abstract fun bindsGetCharacterFavoriteUseCase(
            useCase: GetCharacterFavoriteUseCase
    ): IGetCharacterFavoriteUseCase

    @Binds
    abstract fun bindsSetCharacterFavoriteUseCase(
            useCase: SetCharacterFavoriteUseCase
    ): ISetCharacterFavoriteUseCase

    @Binds
    abstract fun bindsGetAllCharactersFavoriteUseCase(
            useCase: GetAllCharactersFavoriteUseCase
    ): IGetAllCharactersFavoriteUseCase

    @Binds
    abstract fun bindsDeleteCharacterFavoriteUseCase(
            useCase: DeleteCharacterFavoriteUseCase
    ): IDeleteCharacterFavoriteUseCase

    companion object {

        @Singleton
        @Provides
        fun provideMarvelDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                MarvelDatabase::class.java,
                "characters-db"
            ).addMigrations(MIGRATION_1_2)
                .build()

        @Singleton
        @Provides
        fun provideCharacterFavoriteDao(marvelDatabase: MarvelDatabase) =
            marvelDatabase.characterFavoriteDao()
    }
}
