package tools.forma.examples.android.apiimpl.root

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import tools.forma.examples.android.apiimpl.feature.hello.api.HelloMessage
import tools.forma.examples.android.apiimpl.feature.hello.impl.DefaultHelloMessage

class HelloActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val msg: HelloMessage = DefaultHelloMessage()
        val tv = TextView(this)
        tv.text = msg.text()
        setContentView(tv)
    }
}
