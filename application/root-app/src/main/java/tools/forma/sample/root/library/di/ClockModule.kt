package tools.forma.sample.root.library.di

import tools.forma.sample.common.util.clock.Clock
import tools.forma.sample.core.di.library.scopes.AppScope
import tools.forma.sample.root.library.clock.AppClock
import dagger.Module
import dagger.Provides

@Module
class ClockModule {

    @AppScope
    @Provides
    fun provideClock(): Clock =
        AppClock()
}