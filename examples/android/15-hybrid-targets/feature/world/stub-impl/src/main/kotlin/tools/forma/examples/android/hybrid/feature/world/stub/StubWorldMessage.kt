package tools.forma.examples.android.hybrid.feature.world.stub

import tools.forma.examples.android.hybrid.feature.world.api.WorldMessage

/** Lightweight stand-in for [tools.forma.examples.android.hybrid.feature.world.impl.DefaultWorldMessage]. */
class StubWorldMessage : WorldMessage {
    override fun text(): String = "WorldStub"
}
