import tools.forma.android.config.CMake
import tools.forma.android.config.NdkAbi

// packageName cannot end with Java keyword "native" — AGP namespace rules.
// Project dir/suffix stays `native` for Forma validators.
androidNative(
    packageName = "tools.forma.examples.android.ndk.common.hello.ndk",
    buildSystem = CMake(path = file("src/main/cpp/CMakeLists.txt")),
    abi = setOf(NdkAbi.ARM8, NdkAbi.X86_64),
)
