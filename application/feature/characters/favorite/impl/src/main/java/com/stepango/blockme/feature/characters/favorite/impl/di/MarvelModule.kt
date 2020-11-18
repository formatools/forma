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

package com.stepango.blockme.feature.characters.favorite.impl.di

import android.content.Context
import androidx.room.Room
import com.stepango.blockme.feature.characters.favorite.api.domain.repository.ICharacterFavoriteRepository
import com.stepango.blockme.feature.characters.favorite.impl.data.database.MarvelDatabase
import com.stepango.blockme.feature.characters.favorite.impl.data.database.migrations.MIGRATION_1_2
import com.stepango.blockme.feature.characters.favorite.impl.data.repository.CharacterFavoriteRepository
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
