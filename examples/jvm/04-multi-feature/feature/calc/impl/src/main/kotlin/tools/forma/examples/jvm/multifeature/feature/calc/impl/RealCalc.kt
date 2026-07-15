package tools.forma.examples.jvm.multifeature.feature.calc.impl

import tools.forma.examples.jvm.multifeature.feature.calc.api.Calculator

class RealCalc : Calculator {
    override fun add(a: Int, b: Int) = a + b
}
