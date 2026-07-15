package tools.forma.examples.jvm.apiimpl.feature.greeter.impl

import tools.forma.examples.jvm.apiimpl.feature.greeter.api.Greeter

class RealGreeter : Greeter {
    override fun greet(name: String): String = "Hello, $name (from impl)"
}
