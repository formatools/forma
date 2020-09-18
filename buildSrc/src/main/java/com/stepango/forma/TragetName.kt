package com.stepango.forma

sealed class TargetName(val suffix: String)

object Binary : TargetName("")
object Library : TargetName("-library")
object Util : TargetName("-util")
object TestUtil : TargetName("-test-util")
object AndroidTestUtil : TargetName("-android-test-util")
object Databinding : TargetName("-databinding")
object Feature : TargetName("-feature")
object Res : TargetName("-res")
object View : TargetName("-view")
object Api : TargetName("-api")
object Impl : TargetName("-impl")
