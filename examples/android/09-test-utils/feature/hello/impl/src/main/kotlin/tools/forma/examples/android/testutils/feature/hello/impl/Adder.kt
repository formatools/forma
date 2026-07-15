package tools.forma.examples.android.testutils.feature.hello.impl

import tools.forma.examples.android.testutils.feature.hello.api.HelloApi

object Adder : HelloApi {
    fun add(a: Int, b: Int): Int = a + b
}
