package tools.forma.examples.jvm.libutil.binary

import tools.forma.examples.jvm.libutil.feature.greeter.api.Greeter
import tools.forma.examples.jvm.libutil.feature.greeter.impl.RealGreeter

fun main() {
    val greeter: Greeter = RealGreeter()
    println(greeter.greet("World"))
    println("JVM 03 (library+util) build + run successful.")
}
