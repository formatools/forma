package tools.forma.android.dependencies

import Forma
import dep
import deps

object versions {
    object jetbrains {
        const val annotations = "20.0.0"
    }
}

object jetbrains {
    val annotations = "org.jetbrains:annotations:${versions.jetbrains.annotations}".dep
}

object kotlin {
    val reflect =
        deps("org.jetbrains.kotlin:kotlin-reflect:${Forma.settings.kotlinVersion}".dep)
}
