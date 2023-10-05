package tools.forma.sample.feature.home.impl.di

import androidx.lifecycle.ViewModel
import tools.forma.sample.core.di.library.scopes.FeatureScope
import tools.forma.sample.core.mvvm.library.di.ViewModelKey
import tools.forma.sample.feature.home.impl.ui.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface HomeModule {

    @FeatureScope
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    fun bindsHomeViewModel(viewModel: HomeViewModel): ViewModel
}