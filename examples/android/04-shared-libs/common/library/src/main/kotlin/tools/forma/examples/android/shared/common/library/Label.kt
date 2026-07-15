package tools.forma.examples.android.shared.common.library

import tools.forma.examples.android.shared.common.util.exclaim

data class Label(val value: String) {
    fun loud(): String = value.exclaim()
}
