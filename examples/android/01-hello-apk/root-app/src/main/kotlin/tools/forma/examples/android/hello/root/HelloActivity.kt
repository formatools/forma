package tools.forma.examples.android.hello.root

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class HelloActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = "Forma Android 01 hello-apk"
        setContentView(tv)
    }
}
