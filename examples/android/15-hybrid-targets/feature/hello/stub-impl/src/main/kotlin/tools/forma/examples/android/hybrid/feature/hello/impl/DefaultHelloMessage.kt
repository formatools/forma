package tools.forma.examples.android.hybrid.feature.hello.impl

import tools.forma.examples.android.hybrid.feature.hello.api.HelloMessage

/**
 * Classpath replacement for production [DefaultHelloMessage] when
 * `useFeatureStubs` is on (same FQN, lighter body — F-104 simple swap).
 */
class DefaultHelloMessage : HelloMessage {
    override fun text(): String = "HelloStub"
}
