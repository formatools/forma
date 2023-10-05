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

package tools.forma.sample.feature.home.impl.di

import tools.forma.sample.core.di.library.scopes.FeatureScope
import tools.forma.sample.core.theme.android.util.di.ThemeComponent
import tools.forma.sample.feature.home.impl.ui.HomeFragment
import dagger.Component

@FeatureScope
@Component(
    modules = [HomeModule::class],
    dependencies = [ThemeComponent::class])
interface HomeComponent {

    fun inject(homeFragment: HomeFragment)

    @Component.Factory
    interface Factory {

        fun create(themeComponent: ThemeComponent): HomeComponent
    }
}
