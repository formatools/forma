package tools.forma.examples.android.metro.root

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import dev.zacsweers.metro.createGraph

class HelloActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val graph = createGraph<AppGraph>()
        val tv = TextView(this)
        tv.text = graph.message.text()
        setContentView(tv)
    }
}
