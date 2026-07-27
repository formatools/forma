package tools.forma.examples.android.hybrid.feature.hello.impl

import tools.forma.examples.android.hybrid.feature.hello.api.HelloMessage

class DefaultHelloMessage : HelloMessage {
    override fun text(): String = "Hello"
}
