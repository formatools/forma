/*
 * Copyright 2019 vmadalin.com
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

package com.stepango.blockme.core.mvvm.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.stepango.blockme.core.mvvm.library.ui.BaseViewBindingFragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewModelDelegate<VM : ViewModel>(
    private val vmClass: Class<VM>,
    private val ownerProducer: () -> ViewModelStoreOwner,
    private val factoryProducer: () -> ViewModelProvider.Factory
) : ReadOnlyProperty<ViewModelStoreOwner, VM> {

    override fun getValue(thisRef: ViewModelStoreOwner, property: KProperty<*>): VM =
        ViewModelProvider(ownerProducer(), factoryProducer()).get(vmClass)
}

inline fun <reified VM : ViewModel> BaseViewBindingFragment.viewModels(
    noinline ownerProducer: () -> ViewModelStoreOwner = { this },
    noinline factoryProducer: () -> ViewModelProvider.Factory = { viewModelFactory }
): ViewModelDelegate<VM> =
    ViewModelDelegate(VM::class.java, ownerProducer, factoryProducer)
