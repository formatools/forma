package com.stepango.forma.visibility

sealed class Visibility(name: String)

object Public : Visibility("public")

class Package(vararg name: String) : Visibility(name.joinToString(":"))