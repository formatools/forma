package tools.forma.android.utils

import com.android.build.api.dsl.ExternalNativeBuild
import com.android.build.gradle.internal.dsl.DefaultConfig
import com.android.build.gradle.internal.dsl.ExternalNativeBuildOptions
import tools.forma.android.config.CMake
import tools.forma.android.config.NdkAbi
import tools.forma.android.config.NdkBuild

internal fun DefaultConfig.applyFrom(abi: Set<NdkAbi>) {
    if (abi.isNotEmpty()) {
        val result = abi.map(NdkAbi::abiName).toTypedArray()
        ndk.abiFilters(*result)
    }
}

internal fun ExternalNativeBuild.applyFrom(cmake: CMake) {
    cmake {
        path = cmake.path
        version = cmake.version
        cmake.buildStagingDirectory?.let(::buildStagingDirectory)
    }
}

internal fun ExternalNativeBuild.applyFrom(ndkBuild: NdkBuild) {
    ndkBuild {
        path = ndkBuild.path
        ndkBuild.buildStagingDirectory?.let(::buildStagingDirectory)
    }
}

internal fun ExternalNativeBuildOptions.applyFrom(cmake: CMake) {
    cmake {
        val opt = cmake.options
        arguments(*opt.arguments.toTypedArray())
        cFlags(*opt.cflags.toTypedArray())
        cppFlags(*opt.cppflags.toTypedArray())
        if (targets.isNotEmpty()) {
            targets(*opt.targets.toTypedArray())
        }
    }
}

internal fun ExternalNativeBuildOptions.applyFrom(ndkBuild: NdkBuild) {
    ndkBuild {
        val opt = ndkBuild.options
        arguments(*opt.arguments.toTypedArray())
        cFlags(*opt.cflags.toTypedArray())
        cppFlags(*opt.cppflags.toTypedArray())
        if (targets.isNotEmpty()) {
            targets(*opt.targets.toTypedArray())
        }
    }
}
