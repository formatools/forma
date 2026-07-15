package tools.forma.examples.jvm.libutil.common.library

object Ids {
    fun next(prefix: String): String = "$prefix-${(0..999).random()}"
}
