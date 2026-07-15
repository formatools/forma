package tools.forma.examples.jvm.apiimpl.binary

import tools.forma.examples.jvm.apiimpl.feature.greeter.api.Greeter
import tools.forma.examples.jvm.apiimpl.feature.greeter.impl.RealGreeter

fun main() {
    val greeter: Greeter = RealGreeter()
    println(greeter.greet("World"))
    println("JVM 02 (api+impl) build + run successful.")
}
