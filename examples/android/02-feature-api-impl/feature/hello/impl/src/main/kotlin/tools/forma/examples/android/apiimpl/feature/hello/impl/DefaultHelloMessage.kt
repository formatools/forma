package tools.forma.examples.android.apiimpl.feature.hello.impl

import tools.forma.examples.android.apiimpl.feature.hello.api.HelloMessage

class DefaultHelloMessage : HelloMessage {
    override fun text(): String = "Forma Android 02 feature api+impl"
}
