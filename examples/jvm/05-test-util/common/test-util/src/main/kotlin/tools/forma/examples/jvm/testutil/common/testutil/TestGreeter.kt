package tools.forma.examples.jvm.testutil.common.testutil

// Test helper (pure, no production api dep — testUtil may only depend on util/test-util per matrix).
// Consumed from impl's testDependencies.
object TestSupport {
    fun greetingFor(name: String): String = "TEST:$name"
}

