package tools.forma.sample.root.library.di

import tools.forma.sample.core.di.library.scopes.AppScope
import tools.forma.sample.core.network.library.Config
import tools.forma.sample.root.library.config.NetworkConfig
import dagger.Module
import dagger.Provides

@Module
class ConfigModule {

    @AppScope
    @Provides
    fun provideNetworkConfig(): Config =
        NetworkConfig()
}