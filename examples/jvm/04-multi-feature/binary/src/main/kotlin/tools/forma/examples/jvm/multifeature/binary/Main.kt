package tools.forma.examples.jvm.multifeature.binary

import tools.forma.examples.jvm.multifeature.feature.greeter.api.Greeter
import tools.forma.examples.jvm.multifeature.feature.greeter.impl.RealGreeter
import tools.forma.examples.jvm.multifeature.feature.calc.api.Calculator
import tools.forma.examples.jvm.multifeature.feature.calc.impl.RealCalc

fun main() {
    val g: Greeter = RealGreeter()
    val c: Calculator = RealCalc()
    println(g.greet("World"))
    println("2+3=${c.add(2,3)}")
    println("JVM 04 (multi-feature, no impl->impl) successful.")
}
