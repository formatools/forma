package com.stepango.blockme.feature.characters.core.impl.di

import com.stepango.blockme.core.network.library.Config
import com.stepango.blockme.core.network.library.di.NetworkModule
import com.stepango.blockme.feature.characters.core.api.di.CharactersCoreFeature
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    CharactersCoreModule::class,
    NetworkModule::class
])
interface CharactersCoreComponent : CharactersCoreFeature {

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance config: Config): CharactersCoreComponent
    }

}