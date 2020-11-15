package tools.forma.android.visibility

sealed class Visibility(name: String)

object Public : Visibility("public")

class Package(vararg name: String) : Visibility(name.joinToString(":"))