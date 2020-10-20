package com.stepango.forma.dependencies

import Forma
import transitiveDeps
import kapt

// TODO not sure if we need all dependencies here

object dataBinding {
    val viewBinding = transitiveDeps(
        "androidx.databinding:viewbinding:${Forma.configuration.agpVersion}"
    )

    val runtime = transitiveDeps(
        "androidx.databinding:databinding-runtime:${Forma.configuration.agpVersion}"
    )

    val common = transitiveDeps(
        "androidx.databinding:databinding-common:${Forma.configuration.agpVersion}"
    )

    val adapters = transitiveDeps(
        "androidx.databinding:databinding-adapters:${Forma.configuration.agpVersion}"
    )

    val compiler = kapt(
        "androidx.databinding:databinding-compiler:${Forma.configuration.agpVersion}".kapt
    )
}
