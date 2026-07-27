package tools.forma.examples.android.hybrid.root

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import tools.forma.examples.android.hybrid.feature.hello.api.HelloMessage
import tools.forma.examples.android.hybrid.feature.hello.impl.DefaultHelloMessage
import tools.forma.examples.android.hybrid.feature.world.api.WorldMessage
import tools.forma.examples.android.hybrid.feature.world.impl.DefaultWorldMessage

/**
 * Composition root always imports production FQNs. [featureImplementation] +
 * `useFeatureStubs` swaps which module supplies those classes (impl vs stub-impl).
 *
 * - default → "Hello World"
 * - `-Pforma.useFeatureStubs=true` → "HelloStub WorldStub"
 */
class HelloActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val hello: HelloMessage = DefaultHelloMessage()
        val world: WorldMessage = DefaultWorldMessage()
        val tv = TextView(this)
        tv.text = hello.text() + " " + world.text()
        setContentView(tv)
    }
}
