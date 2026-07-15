package tools.forma.examples.jvm.testutil.binary

import tools.forma.examples.jvm.testutil.feature.greeter.api.Greeter
import tools.forma.examples.jvm.testutil.feature.greeter.impl.RealGreeter

fun main() {
    val g: Greeter = RealGreeter()
    println(g.greet("World"))
    println("JVM 05 (test-util) build + run successful.")
}
