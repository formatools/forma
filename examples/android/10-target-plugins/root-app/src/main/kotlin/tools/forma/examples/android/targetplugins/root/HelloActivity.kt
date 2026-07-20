package tools.forma.examples.android.targetplugins.root

import android.app.Activity
import android.os.Bundle
import tools.forma.examples.android.targetplugins.feature.hello.impl.HelloScreen

class HelloActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(HelloScreen(this).view)
    }
}
