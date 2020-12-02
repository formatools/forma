package com.stepango.blockme.core.theme.android.util.di

import com.stepango.blockme.core.theme.android.util.ThemeUtils
import com.stepango.blockme.core.theme.android.util.ThemeUtilsImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class ThemeModule {

    @Singleton
    @Binds
    abstract fun bindThemeUtils(themeUtilsImpl: ThemeUtilsImpl): ThemeUtils
}
