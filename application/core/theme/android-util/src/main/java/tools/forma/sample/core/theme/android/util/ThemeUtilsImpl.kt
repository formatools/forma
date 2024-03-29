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

package tools.forma.sample.core.theme.android.util

import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate
import javax.inject.Inject

class ThemeUtilsImpl @Inject constructor() : ThemeUtils {

    override fun isDarkTheme(context: Context) = context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

    override fun isLightTheme(context: Context) = !isDarkTheme(context)

    override fun setNightMode(forceNight: Boolean, delay: Long) {
        Handler().postDelayed({
            AppCompatDelegate.setDefaultNightMode(
                if (forceNight) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }, delay)
    }
}
