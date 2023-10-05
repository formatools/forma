package tools.forma.sample.core.theme.android.util.di

import tools.forma.sample.core.theme.android.util.ThemeUtils
import tools.forma.sample.core.theme.android.util.ThemeUtilsImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class ThemeModule {

    @Singleton
    @Binds
    abstract fun bindThemeUtils(themeUtilsImpl: ThemeUtilsImpl): ThemeUtils
}
