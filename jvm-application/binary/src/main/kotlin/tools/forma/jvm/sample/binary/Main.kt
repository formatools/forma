package tools.forma.jvm.sample.binary

import tools.forma.jvm.sample.feature.greeter.api.Greeter
import tools.forma.jvm.sample.feature.greeter.impl.GreeterImpl
import tools.forma.jvm.sample.feature.calculator.api.Calculator
import tools.forma.jvm.sample.feature.calculator.impl.CalculatorImpl

/**
 * Composition root demo for F-031.
 * Wires impls (greeter + calculator) + shared library/util only at the binary.
 * Cross-feature communication happens exclusively through api modules.
 */
fun main() {
    val greeter: Greeter = GreeterImpl()
    val calculator: Calculator = CalculatorImpl()

    val greeting = greeter.greet("world")
    val sum = calculator.add(2, 3)

    println("$greeting (2 + 3 = $sum)")
    println("JVM sample (tools.forma.jvm) build + run successful.")
}
