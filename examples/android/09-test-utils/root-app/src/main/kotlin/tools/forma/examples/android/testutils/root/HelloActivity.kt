package tools.forma.examples.android.testutils.root

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import tools.forma.examples.android.testutils.feature.hello.impl.Adder

class HelloActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = "Forma Android 09 test utils sum=" + Adder.add(2, 3)
        setContentView(tv)
    }
}
