package com.stepango.blockme.root.library.di

import com.stepango.blockme.common.util.clock.Clock
import com.stepango.blockme.core.di.library.scopes.AppScope
import com.stepango.blockme.root.library.clock.AppClock
import dagger.Module
import dagger.Provides

@Module
class ClockModule {

    @AppScope
    @Provides
    fun provideClock(): Clock =
        AppClock()
}