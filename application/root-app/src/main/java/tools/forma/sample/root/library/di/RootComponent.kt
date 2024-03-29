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

package tools.forma.sample.root.library.di

import tools.forma.sample.core.di.library.BaseComponent
import tools.forma.sample.core.di.library.scopes.AppScope
import tools.forma.sample.core.theme.android.util.di.ThemeComponent
import tools.forma.sample.root.library.SampleApp
import dagger.Component

/**
 * App component that application component's components depend on.
 *
 * @see Component
 */
@AppScope
@Component(
    dependencies = [
        ThemeComponent::class,
        BaseComponent::class
    ],
    modules = [
        ConfigModule::class,
        ClockModule::class,
    ]
)
interface RootComponent {

    /**
     * Inject dependencies on application.
     *
     * @param application The sample application.
     */
    fun inject(application: SampleApp)
}
