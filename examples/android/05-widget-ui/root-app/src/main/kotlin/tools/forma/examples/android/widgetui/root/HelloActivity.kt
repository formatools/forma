package tools.forma.examples.android.widgetui.root

import android.app.Activity
import android.os.Bundle
import tools.forma.examples.android.widgetui.common.banner.widget.BannerView

class HelloActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(BannerView(this).also { it.setMessage("Forma Android 05 widget + uiLibrary") })
    }
}
