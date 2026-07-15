package tools.forma.examples.android.widgetui.common.banner.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import tools.forma.examples.android.widgetui.common.ui.library.UiTokens

class BannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : TextView(context, attrs) {
    init {
        val pad = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            UiTokens.paddingDp.toFloat(),
            resources.displayMetrics
        ).toInt()
        setPadding(pad, pad, pad, pad)
        textSize = 18f
    }

    fun setMessage(value: String) {
        text = value
    }
}
