package tools.forma.jvm.sample.common.testutil

// Shared test helpers (exercises jvm.test-util target + matrix).
// No src/test sources in this minimal sample; the module is declared via testDependencies
// on impls if needed in future. Presence proves registration and build.
object TestHelpers {
    fun echo(s: String): String = s
}
