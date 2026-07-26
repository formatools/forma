#include <jni.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_tools_forma_examples_android_ndk_common_hello_androidutil_HelloNative_greeting(
        JNIEnv *env,
        jclass /* clazz */) {
    return env->NewStringUTF("Forma Android 13 androidNative + JNI");
}
