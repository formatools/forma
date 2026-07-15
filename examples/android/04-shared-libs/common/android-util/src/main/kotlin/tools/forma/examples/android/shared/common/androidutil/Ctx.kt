package tools.forma.examples.android.shared.common.androidutil

import android.content.Context

fun Context.appLabel(): String = applicationInfo.loadLabel(packageManager).toString()
