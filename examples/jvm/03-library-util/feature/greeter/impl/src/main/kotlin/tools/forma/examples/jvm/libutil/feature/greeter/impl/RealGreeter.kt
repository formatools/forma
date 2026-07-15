package tools.forma.examples.jvm.libutil.feature.greeter.impl

import tools.forma.examples.jvm.libutil.common.util.greetPrefix
import tools.forma.examples.jvm.libutil.common.library.Ids
import tools.forma.examples.jvm.libutil.feature.greeter.api.Greeter

class RealGreeter : Greeter {
    override fun greet(name: String): String =
        "${greetPrefix(name)} [${Ids.next("g")}] (from impl+util+lib)"
}
