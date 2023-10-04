package tools.forma.sample.feature.characters.core.impl.di

import tools.forma.sample.common.util.clock.Clock
import tools.forma.sample.core.network.library.Config
import tools.forma.sample.core.network.library.di.NetworkModule
import tools.forma.sample.feature.characters.core.api.di.CharactersCoreFeature
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

        fun create(@BindsInstance config: Config, @BindsInstance clock: Clock): CharactersCoreComponent
    }
}