package tools.forma.examples.jvm.multifeature.feature.greeter.impl

import tools.forma.examples.jvm.multifeature.feature.greeter.api.Greeter

class RealGreeter : Greeter {
    override fun greet(name: String) = "Hello $name (greeter)"
}
