package tools.forma.examples.android.ndk.common.hello.androidutil

/**
 * JNI façade over first-party [androidNative] module (`libforma_hello.so`).
 * Pattern: `androidUtil` owns Kotlin bindings; `androidNative` owns C/CMake.
 */
object HelloNative {
    init {
        System.loadLibrary("forma_hello")
    }

    @JvmStatic
    external fun greeting(): String
}
