package com.stepango.blockme.root.library.di

import com.stepango.blockme.core.di.library.scopes.AppScope
import com.stepango.blockme.core.network.library.Config
import com.stepango.blockme.root.library.config.NetworkConfig
import dagger.Module
import dagger.Provides

@Module
class ConfigModule {

	@AppScope
	@Provides
	fun provideNetworkConfig(): Config =
		NetworkConfig()
}