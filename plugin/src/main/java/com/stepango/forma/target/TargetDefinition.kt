package com.stepango.forma.target

sealed class TargetDefinition(val suffix: String)

object BinaryTarget : TargetDefinition("binary")
object ApplicationTarget : TargetDefinition("app")
object LibraryTarget : TargetDefinition("library")
object UtilTarget : TargetDefinition("util")
object TestUtilTarget : TargetDefinition("test-util")
object AndroidTestUtilTarget : TargetDefinition("android-test-util")
object AndroidUtilTarget : TargetDefinition("android-util")
object DataBindingTarget : TargetDefinition("databinding")
object DataBindingAdapterTarget : TargetDefinition("databinding-adapter")
object ResourcesTarget : TargetDefinition("res")
object ApiTarget : TargetDefinition("api")
object ImplTarget : TargetDefinition("impl")
object WidgetTarget : TargetDefinition("widget")
