package tools.forma.android.target

import tools.forma.target.FormaTarget

sealed class TargetSpec(override val suffix: String): FormaTarget

object BinaryTarget : TargetSpec("binary")
object ApplicationTarget : TargetSpec("app")
object LibraryTarget : TargetSpec("library")
object UtilTarget : TargetSpec("util")
object TestUtilTarget : TargetSpec("test-util")
object AndroidTestUtilTarget : TargetSpec("android-test-util")
object AndroidUtilTarget : TargetSpec("android-util")
object DataBindingTarget : TargetSpec("databinding")
object DataBindingAdapterTarget : TargetSpec("databinding-adapter")
object ResourcesTarget : TargetSpec("res")
object ApiTarget : TargetSpec("api")
object ImplTarget : TargetSpec("impl")
object WidgetTarget : TargetSpec("widget")
