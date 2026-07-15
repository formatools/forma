package tools.forma.examples.android.catalog.feature.hello.impl

import timber.log.Timber
import tools.forma.examples.android.catalog.feature.hello.api.HelloApi

object HelloLogger : HelloApi {
    fun log(msg: String) {
        Timber.d("hello: %s", msg)
    }
}
