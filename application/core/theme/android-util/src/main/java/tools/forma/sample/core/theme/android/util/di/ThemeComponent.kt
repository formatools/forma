package tools.forma.sample.core.theme.android.util.di

import tools.forma.sample.core.theme.android.util.ThemeUtils
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ThemeModule::class
])
interface ThemeComponent {

    fun themeUtils(): ThemeUtils

    @Component.Factory
    interface Factory {

        fun create(): ThemeComponent
    }
}

interface ThemeComponentProvider {

    fun getThemeComponent(): ThemeComponent
}
