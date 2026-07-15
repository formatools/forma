package tools.forma.examples.jvm.testutil.feature.greeter.impl

import tools.forma.examples.jvm.testutil.feature.greeter.api.Greeter

class RealGreeter : Greeter {
    override fun greet(name: String) = "Hello $name (real)"
}
