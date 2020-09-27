package com.stepango.forma.dependencies

import dep
import deps
import jetbrains
import versions

object kotlin {
    val stdlib_common = "org.jetbrains.kotlin:kotlin-stdlib-common:${versions.jetbrains.kotlin}".dep
    val stdlib = deps(
        "org.jetbrains.kotlin:kotlin-stdlib:${versions.jetbrains.kotlin}".dep,
        jetbrains.annotations,
        stdlib_common
    )
    val stdlib_jdk7 = deps(
        "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${versions.jetbrains.kotlin}".dep,
        stdlib
    )
    val stdlib_jdk8 = deps(
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${versions.jetbrains.kotlin}".dep,
        stdlib,
        stdlib_jdk7
    )
    val reflect = deps(
        "org.jetbrains.kotlin:kotlin-reflect:${versions.jetbrains.kotlin}".dep,
        stdlib
    )
}