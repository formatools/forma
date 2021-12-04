package com.stepango.blockme.feature.characters.core.impl.di

import com.stepango.blockme.common.util.clock.Clock
import com.stepango.blockme.core.network.library.Config
import com.stepango.blockme.core.network.library.di.NetworkModule
import com.stepango.blockme.feature.characters.core.api.di.CharactersCoreApi
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CharactersCoreModule::class,
        NetworkModule::class
    ]
)
interface CharactersCoreComponent : CharactersCoreApi {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance config: Config, @BindsInstance clock: Clock): CharactersCoreComponent
    }
}