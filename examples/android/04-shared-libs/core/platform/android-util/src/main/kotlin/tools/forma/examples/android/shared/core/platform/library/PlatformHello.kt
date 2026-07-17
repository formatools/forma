package tools.forma.examples.android.shared.core.platform.library

import android.content.Context
import tools.forma.examples.android.shared.common.androidutil.appLabel
import tools.forma.examples.android.shared.common.library.Label

object PlatformHello {
    fun text(context: Context): String =
        Label("shared-libs " + context.appLabel()).loud()
}
