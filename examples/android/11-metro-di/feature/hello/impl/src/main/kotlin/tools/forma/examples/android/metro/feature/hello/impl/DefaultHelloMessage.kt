package tools.forma.examples.android.metro.feature.hello.impl

import dev.zacsweers.metro.Inject
import tools.forma.examples.android.metro.feature.hello.api.HelloMessage

/** Injected feature implementation (Metro constructor injection). */
@Inject
class DefaultHelloMessage(
    private val clock: GreetingClock,
) : HelloMessage {
    override fun text(): String = "Forma + Metro · t=${clock.nowMillis()}"
}

/** Small injectable collaborator to prove multi-binding constructor graphs. */
@Inject
class GreetingClock {
    fun nowMillis(): Long = System.currentTimeMillis()
}
