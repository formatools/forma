package com.stepango.blockme.core.di.library

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
internal object BaseModule {

    @Provides
    fun provideContext(application: Application): Context = application
}