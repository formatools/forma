package tools.forma.examples.android.multi.root

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import tools.forma.examples.android.multi.feature.hello.api.HelloApi
import tools.forma.examples.android.multi.feature.hello.impl.HelloImpl
import tools.forma.examples.android.multi.feature.settings.api.SettingsApi
import tools.forma.examples.android.multi.feature.settings.impl.SettingsImpl

class HelloActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val hello: HelloApi = HelloImpl()
        val settings: SettingsApi = SettingsImpl()
        val tv = TextView(this)
        tv.text = hello.greet() + " | " + settings.themeName()
        setContentView(tv)
    }
}
