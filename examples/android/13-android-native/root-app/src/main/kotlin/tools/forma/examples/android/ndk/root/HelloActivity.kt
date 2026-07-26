package tools.forma.examples.android.ndk.root

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import tools.forma.examples.android.ndk.common.hello.androidutil.HelloNative

class HelloActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = HelloNative.greeting()
        setContentView(tv)
    }
}
