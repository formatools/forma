package tools.forma.examples.android.shared.feature.hello.impl

import android.content.Context
import tools.forma.examples.android.shared.common.library.Label
import tools.forma.examples.android.shared.core.platform.library.PlatformHello
import tools.forma.examples.android.shared.feature.hello.api.HelloApi

object HelloFacade : HelloApi {
    override fun label(): Label = Label("feature")
    fun message(context: Context): String =
        label().loud() + " / " + PlatformHello.text(context)
}
