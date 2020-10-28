package com.stepango.blockme.core.di.library

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component

@Component(modules = [BaseModule::class])
interface BaseComponent {

    fun getContext(): Context

    fun getApplication(): Application

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance application: Application): BaseComponent
    }
}

interface BaseComponentProvider {

    fun getBaseComponent(): BaseComponent
}