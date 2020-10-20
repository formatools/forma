package com.stepango.blockme.feature.home.impl.di

import androidx.lifecycle.ViewModel
import com.stepango.blockme.core.di.library.scopes.FeatureScope
import com.stepango.blockme.core.mvvm.library.di.ViewModelKey
import com.stepango.blockme.feature.home.impl.ui.HomeViewModel
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