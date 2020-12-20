#include <jni.h>
#include <string>
#include <vector>
#include <dirent.h>
#include <sys/stat.h>
#include "Log.h"

static const char* kClassPathName = "com/stepango/blockme/root/library/AppStartup";
static const std::vector<std::string> allowedPackages = { // NOLINT(cert-err58-cpp)
        "com.stepango.blockme.app"
};

jstring getPackageName(JNIEnv* env, jobject context) {
    jclass m_context = env->GetObjectClass(context);
    jmethodID midGetPackageName = env->GetMethodID(m_context, "getPackageName","()Ljava/lang/String;");
    return (jstring) env->CallObjectMethod(context, midGetPackageName);
}

extern "C" JNIEXPORT jboolean jniCheckPackageName(
    JNIEnv *env,
    jobject /* this */,
    jobject context
) {
    jstring jPackage = getPackageName(env, context);
    const char *packagePtr = env->GetStringUTFChars(jPackage, nullptr);
    std::string package(packagePtr);

    bool isAllowedPackage = false;

    LOGD("Allowed packages name:");
    for (auto str = allowedPackages.begin(); str != allowedPackages.end(); ++str) {
        LOGD("- %s", str->c_str());
    }

    if (find(allowedPackages.begin(), allowedPackages.end(), package) != allowedPackages.end()) {
        LOGD("Found allowed package - %s", package.c_str());
        isAllowedPackage = true;
    } else {
        LOGD("Couldn't found allowed package - %s", package.c_str());
    }

    env->ReleaseStringUTFChars(jPackage, packagePtr);
    return (jboolean) isAllowedPackage;
}

static JNINativeMethod gMethods[] = {
        { "nativeCheckPackage", "(Landroid/content/Context;)Z", (void*) jniCheckPackageName }
};

#ifndef NELEM
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif

jint JNI_OnLoad(JavaVM* vm, void* /* reserved */)
{
    JNIEnv* env = nullptr;
    jint result = -1;

    if (vm->GetEnv((void**) &env, JNI_VERSION_1_6) != JNI_OK)
        return result;

    jclass clazz = env->FindClass(kClassPathName);
    if (clazz == nullptr || env->RegisterNatives(clazz, gMethods, NELEM(gMethods)) < 0)
        return result;

    return JNI_VERSION_1_6;
}
