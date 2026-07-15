package tools.forma.examples.android.catalog.root

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import timber.log.Timber
import tools.forma.examples.android.catalog.feature.hello.impl.HelloLogger

class HelloActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        HelloLogger.log("catalog step")
        val tv = TextView(this)
        tv.text = "Forma Android 08 deps catalog"
        setContentView(tv)
    }
}
