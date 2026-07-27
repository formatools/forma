package tools.forma.examples.android.hybrid.feature.world.impl

import tools.forma.examples.android.hybrid.feature.world.api.WorldMessage

/**
 * Classpath replacement for production [DefaultWorldMessage] when
 * `useFeatureStubs` is on (same FQN, lighter body — F-104 simple swap).
 */
class DefaultWorldMessage : WorldMessage {
    override fun text(): String = "WorldStub"
}
