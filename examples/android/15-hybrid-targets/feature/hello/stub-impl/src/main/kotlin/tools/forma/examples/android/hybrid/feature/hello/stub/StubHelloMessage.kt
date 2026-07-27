package tools.forma.examples.android.hybrid.feature.hello.stub

import tools.forma.examples.android.hybrid.feature.hello.api.HelloMessage

/** Lightweight stand-in for [tools.forma.examples.android.hybrid.feature.hello.impl.DefaultHelloMessage]. */
class StubHelloMessage : HelloMessage {
    override fun text(): String = "HelloStub"
}
