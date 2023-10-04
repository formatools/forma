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

package com.stepango.blockme.common.extensions.android.util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import coil.load
import kotlin.random.Random

fun ImageView.loadImage(url: String?, @DrawableRes placeholderId: Int? = null) {
    load(url) {
        crossfade(true)
        placeholder(placeholderId?.let {
            ContextCompat.getDrawable(context, it)
        } ?: run {
            val placeholdersColors = resources.getStringArray(com.stepango.blockme.common.placeholder.res.R.array.placeholders)
            val placeholderColor = placeholdersColors[Random.nextInt(placeholdersColors.size)]
            ColorDrawable(Color.parseColor(placeholderColor))
        })
    }
}
