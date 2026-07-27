package tools.forma.examples.android.hybrid.feature.world.impl

import tools.forma.examples.android.hybrid.feature.world.api.WorldMessage

class DefaultWorldMessage : WorldMessage {
    override fun text(): String = "World"
}
