package tools.forma.examples.android.metro.root

import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import tools.forma.examples.android.metro.feature.hello.api.HelloMessage
import tools.forma.examples.android.metro.feature.hello.impl.DefaultHelloMessage

/**
 * Root Metro graph. Composition stays at androidApp / metroApp — feature `impl`
 * modules do not depend on each other (Forma matrix).
 */
@DependencyGraph
interface AppGraph {
    val message: HelloMessage

    @Provides
    fun provideHelloMessage(impl: DefaultHelloMessage): HelloMessage = impl
}
