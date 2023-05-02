/*
 * Copyright 2023 tinkoff.ru
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stepango.blockme.core.mvvm.library.ui

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.stepango.blockme.core.mvvm.library.lifecycle.ViewModelFactory
import javax.inject.Inject
import kotlin.reflect.KClass

/**
 * Base fragment for view binding with abstract dependency injection
 */
abstract class BaseViewBindingFragment(
    @LayoutRes
    private val layoutId: Int
) : Fragment(layoutId) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    abstract fun onInitDependencyInjection()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onInitDependencyInjection()
    }

    protected fun requireCompatActivity(): AppCompatActivity {
        requireActivity()
        val activity = requireActivity()
        if (activity is AppCompatActivity) {
            return activity
        } else {
            throw TypeCastException("Main activity should extend from 'AppCompatActivity'")
        }
    }

    protected inline fun <reified T : Any> requireProvider(providerType: KClass<T>): T {
        val appContext = requireContext().applicationContext
        if (appContext is T) {
            return appContext
        } else {
            throw TypeCastException("Application should extend from '$providerType'")
        }
    }
}
