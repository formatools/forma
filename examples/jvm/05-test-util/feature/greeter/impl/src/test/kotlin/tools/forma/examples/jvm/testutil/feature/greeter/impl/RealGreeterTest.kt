package tools.forma.examples.jvm.testutil.feature.greeter.impl

import tools.forma.examples.jvm.testutil.common.testutil.TestSupport

// Compile-only test sources to exercise testDependencies + testUtil target.
// No external test framework declared in this minimal teaching example;
// Gradle test task passes with 0 discovered tests.
class GreeterSmoke {
    fun smokeReal() {
        check(RealGreeter().greet("World") == "Hello World (real)")
    }
    fun smokeTestUtil() {
        check(TestSupport.greetingFor("World") == "TEST:World")
    }
}
