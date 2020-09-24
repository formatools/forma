package com.stepango.forma

sealed class ModuleDefinition(val suffix: String)

object BinaryModule : ModuleDefinition("app")
object LibraryModule : ModuleDefinition("library")
object UtilModule : ModuleDefinition("util")
object TestUtilModule : ModuleDefinition("test-util")
object AndroidTestUtilModule : ModuleDefinition("android-test-util")
object DataBindingModule : ModuleDefinition("databinding")
object DataBindingAdapterModule : ModuleDefinition("databinding-adapter")
object FeatureModule : ModuleDefinition("feature")
object ResourcesModule : ModuleDefinition("res")
object ViewModule : ModuleDefinition("view")
object ApiModule : ModuleDefinition("api")
object ImplModule : ModuleDefinition("impl")
object WidgetModule : ModuleDefinition("widget")
