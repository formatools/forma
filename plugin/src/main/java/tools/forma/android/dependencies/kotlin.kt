package tools.forma.android.dependencies

import dep
import deps
import Forma

object versions {
    object jetbrains {
        const val annotations = "20.0.0"
    }
}

object jetbrains {
    val annotations
            = "org.jetbrains:annotations:${versions.jetbrains.annotations}".dep
}

object kotlin {
    val stdlib_common
            = "org.jetbrains.kotlin:kotlin-stdlib-common:${Forma.configuration.kotlinVersion}".dep
    val stdlib = deps(
        "org.jetbrains.kotlin:kotlin-stdlib:${Forma.configuration.kotlinVersion}".dep,
        jetbrains.annotations,
        stdlib_common
    )
    val stdlib_jdk7 = deps(
        "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Forma.configuration.kotlinVersion}".dep,
        stdlib
    )
    val stdlib_jdk8 = deps(
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Forma.configuration.kotlinVersion}".dep,
        stdlib,
        stdlib_jdk7
    )
    val reflect = deps(
        "org.jetbrains.kotlin:kotlin-reflect:${Forma.configuration.kotlinVersion}".dep,
        stdlib
    )
}
