package tools.forma.android.utils

import com.android.build.api.dsl.CmakeFlags
import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.ExternalNativeBuild
import com.android.build.api.dsl.ExternalNativeBuildFlags
import com.android.build.api.dsl.NdkBuildFlags
import tools.forma.android.config.CMake
import tools.forma.android.config.NdkAbi
import tools.forma.android.config.NdkBuild as FormaNdkBuild

internal fun DefaultConfig.applyFrom(abi: Set<NdkAbi>) {
    if (abi.isNotEmpty()) {
        ndk.abiFilters.addAll(abi.map(NdkAbi::abiName))
    }
}

internal fun ExternalNativeBuild.applyFrom(cmake: CMake) {
    cmake {
        path = cmake.path
        version = cmake.version
        cmake.buildStagingDirectory?.let { buildStagingDirectory = it }
    }
}

internal fun ExternalNativeBuild.applyFrom(ndkBuild: FormaNdkBuild) {
    ndkBuild {
        path = ndkBuild.path
        ndkBuild.buildStagingDirectory?.let { buildStagingDirectory = it }
    }
}

internal fun ExternalNativeBuildFlags.applyFrom(cmake: CMake) {
    cmake {
        val opt = cmake.options
        arguments(*opt.arguments.toTypedArray())
        cFlags(*opt.cflags.toTypedArray())
        cppFlags(*opt.cppflags.toTypedArray())
        if (opt.targets.isNotEmpty()) {
            targets(*opt.targets.toTypedArray())
        }
    }
}

internal fun ExternalNativeBuildFlags.applyFrom(ndkBuild: FormaNdkBuild) {
    ndkBuild {
        val opt = ndkBuild.options
        arguments(*opt.arguments.toTypedArray())
        cFlags(*opt.cflags.toTypedArray())
        cppFlags(*opt.cppflags.toTypedArray())
        if (opt.targets.isNotEmpty()) {
            targets(*opt.targets.toTypedArray())
        }
    }
}

// Keep type aliases so accidental imports of flags helpers stay clear
@Suppress("unused")
private typealias CmakeFlagsRef = CmakeFlags
@Suppress("unused")
private typealias NdkBuildFlagsRef = NdkBuildFlags
