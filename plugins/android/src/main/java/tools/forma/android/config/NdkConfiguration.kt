package tools.forma.android.config

import java.io.File

sealed class NdkBuildSystem

data class CMake(
    val path: File,
    val options: NdkSystemOptions = NdkSystemOptions(),
    val version: String? = null,
    val buildStagingDirectory: File? = null
) : NdkBuildSystem()

data class NdkBuild(
    val path: File,
    val options: NdkSystemOptions = NdkSystemOptions(),
    val buildStagingDirectory: File? = null
) : NdkBuildSystem()


data class NdkSystemOptions(
    val cflags: Set<String> = emptySet(),
    val cppflags: Set<String> = emptySet(),
    val arguments: Set<String> = emptySet(),
    val targets: Set<String> = emptySet()
)


enum class NdkAbi(val abiName: String) {
    X86("x86"),
    X86_64("x86_64"),
    ARM7("armeabi-v7a"),
    ARM8("arm64-v8a"),
}
