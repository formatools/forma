package tools.forma.examples.android.resvb.root

import android.app.Activity
import android.os.Bundle
import tools.forma.examples.android.resvb.feature.hello.impl.HelloScreen

class HelloActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(HelloScreen(this).view)
    }
}
