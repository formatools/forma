package tools.forma.examples.android.multi.feature.hello.impl

import tools.forma.examples.android.multi.feature.hello.api.HelloApi

class HelloImpl : HelloApi {
    override fun greet() = "hello-feature"
}
