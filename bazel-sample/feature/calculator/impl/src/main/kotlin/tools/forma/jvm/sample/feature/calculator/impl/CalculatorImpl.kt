package tools.forma.jvm.sample.feature.calculator.impl

import tools.forma.jvm.sample.common.library.Math
import tools.forma.jvm.sample.feature.calculator.api.Calculator

class CalculatorImpl : Calculator {
    override fun add(a: Int, b: Int): Int = Math.add(a, b)
}
