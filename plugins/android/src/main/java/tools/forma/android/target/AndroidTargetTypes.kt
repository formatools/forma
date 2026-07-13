package tools.forma.android.target

import tools.forma.core.target.TargetType
import tools.forma.core.target.targetType

/**
 * Stable TargetType instances for Android platform (F-021).
 * Ids are unique even when nameSuffix collides (e.g. "library" used by JVM + androidLibrary).
 * These live in :android (platform owns concrete types); core holds pure engine.
 *
 * These parallel the legacy *TargetTemplate objects for now (F-021); full registry migration F-023.
 */
object AndroidTargetTypes {
    val api: TargetType = targetType("android.api", "api")
    val impl: TargetType = targetType("android.impl", "impl")
    val library: TargetType = targetType("android.library", "library") // androidLibrary consumer (distinct from jvm.library)
    val jvmLibrary: TargetType = targetType("jvm.library", "library") // JVM library() consumer
    val uiLibrary: TargetType = targetType("android.ui-library", "ui-library")
    val native: TargetType = targetType("android.native", "native")
    val util: TargetType = targetType("jvm.util", "util") // JVM util target (distinct id from android.android-util)
    val testUtil: TargetType = targetType("android.test-util", "test-util")
    val androidTestUtil: TargetType = targetType("android.android-test-util", "android-test-util")
    val androidUtil: TargetType = targetType("android.android-util", "android-util")
    val viewBinding: TargetType = targetType("android.viewbinding", "viewbinding")
    val res: TargetType = targetType("android.res", "res")
    val widget: TargetType = targetType("android.widget", "widget")
    val composeWidget: TargetType = targetType("android.compose-widget", "compose-widget")
    val app: TargetType = targetType("android.app", "app")
    val binary: TargetType = targetType("android.binary", "binary")
}
