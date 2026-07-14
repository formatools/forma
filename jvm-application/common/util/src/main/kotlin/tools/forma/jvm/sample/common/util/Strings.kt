package tools.forma.jvm.sample.common.util

fun greet(name: String): String = "Hello, ${name.lowercase().replaceFirstChar { it.uppercase() }}!"
