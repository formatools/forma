package tools.forma.jvm.sample.feature.greeter.impl

import tools.forma.jvm.sample.common.util.greet as formatGreeting
import tools.forma.jvm.sample.feature.greeter.api.Greeter

class GreeterImpl : Greeter {
    override fun greet(name: String): String = formatGreeting(name)
}
