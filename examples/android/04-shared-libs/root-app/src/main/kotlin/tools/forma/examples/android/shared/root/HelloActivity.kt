package tools.forma.examples.android.shared.root

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import tools.forma.examples.android.shared.feature.hello.impl.HelloFacade

class HelloActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = HelloFacade.message(this)
        setContentView(tv)
    }
}
