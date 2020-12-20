package com.stepango.blockme.root.library

import android.content.Context

object AppStartup {

    init {
        System.loadLibrary("forma-utils")
    }

    fun init(context: Context) {
        if (!nativeCheckPackage(context)) {
            kill()
        }
    }

    private fun kill() {
        Runtime.getRuntime().exit(0)
    }

    private external fun nativeCheckPackage(context: Context): Boolean
}