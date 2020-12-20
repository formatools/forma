#ifndef _LOG_H
#define _LOG_H

#include <android/log.h>
#include <stdarg.h>
#include <stdbool.h>
#include <jni.h>

#define LOG_TAG "FORMA_NATIVE"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__)
#define LOGV_F(prio, tag, fmt, ap) __android_log_vprint(prio, tag, fmt, ap)

#endif