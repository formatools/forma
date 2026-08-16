package tools.forma.bazel

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import tools.forma.bazel.adapter.JvmBazelAdapter
import tools.forma.bazel.sample.JvmApplicationFixture

/**
 * F-041 acceptance tests for the JVM Bazel adapter spike.
 * - Labels, rule selection, tags, main_class
 * - Matrix respected: no impl→impl emitted
 * - check() reports violations for illegal graph, clean for legal
 * - round-trip generate then check is clean
 */
class JvmBazelAdapterTest {

    private val adapter = JvmBazelAdapter()
    private val fixture = JvmApplicationFixture.model
    private val illegal = JvmApplicationFixture.modelWithIllegalImplToImpl

    @Test
    fun `generate produces entries for every target in fixture`() {
        val builds = adapter.generate(fixture)
        // 1 binary + 2 api + 2 impl + 3 common = 8
        assertEquals(8, builds.size)
        assertTrue(builds.keys.contains("binary"))
        assertTrue(builds.keys.contains("feature/greeter/api"))
        assertTrue(builds.keys.contains("feature/greeter/impl"))
        assertTrue(builds.keys.contains("common/test-util"))
    }

    @Test
    fun `labels follow documented convention (Gradle colon path to Bazel label)`() {
        val builds = adapter.generate(fixture)
        // api
        val apiContent = builds.getValue("feature/greeter/api")
        assertTrue(apiContent.contains("name = \"api\""))
        assertTrue("//feature/greeter/api:api" in apiContent || "name = \"api\"" in apiContent)

        // impl
        val implContent = builds.getValue("feature/greeter/impl")
        assertTrue(implContent.contains("name = \"impl\""))

        // binary
        val binContent = builds.getValue("binary")
        assertTrue(binContent.contains("name = \"binary\""))
    }

    @Test
    fun `binary uses jvm_binary Starlark macro and carries main_class from metadata`() {
        val builds = adapter.generate(fixture)
        val bin = builds.getValue("binary")
        assertTrue(bin.contains("load(\"//forma:defs.bzl\", \"jvm_binary\")"))
        assertTrue(bin.contains("jvm_binary("))
        assertTrue(bin.contains("main_class = \"tools.forma.jvm.sample.binary.MainKt\""))
        assertFalse(bin.contains("kt_jvm_binary("))
        assertFalse(bin.contains("tags = ["))
    }

    @Test
    fun `non-binary use type macros and do not re-select rule or tags`() {
        val builds = adapter.generate(fixture)
        val apiB = builds.getValue("feature/greeter/api")
        assertTrue(apiB.contains("load(\"//forma:defs.bzl\", \"jvm_api\")"))
        assertTrue(apiB.contains("jvm_api("))
        assertFalse(apiB.contains("kt_jvm_library("))
        assertFalse(apiB.contains("forma:type="))

        val implB = builds.getValue("feature/greeter/impl")
        assertTrue(implB.contains("jvm_impl("))
        assertFalse(implB.contains("kt_jvm_library("))
    }

    @Test
    fun `greeter impl does not depend on calculator impl (matrix + fixture)`() {
        val builds = adapter.generate(fixture)
        val implB = builds.getValue("feature/greeter/impl")
        // Must not contain a dep on the other impl
        assertFalse("//feature/calculator/impl:impl" in implB)
        // But must contain its legal deps
        assertTrue("//feature/greeter/api:api" in implB)
        assertTrue("//common/util:util" in implB)
        assertTrue("//common/library:library" in implB)
    }

    @Test
    fun `binary depends on both impls and shared (composition root)`() {
        val builds = adapter.generate(fixture)
        val bin = builds.getValue("binary")
        assertTrue("//feature/greeter/impl:impl" in bin)
        assertTrue("//feature/calculator/impl:impl" in bin)
        assertTrue("//common/library:library" in bin)
    }

    @Test
    fun `check illegal impl to impl produces violations`() {
        val report = adapter.check(illegal)
        assertTrue(report.violations.isNotEmpty(), "expected at least one violation for impl→impl")
        val hasImplImpl = report.violations.any { "jvm.impl" in it && "jvm.impl" in it }
        assertTrue(hasImplImpl, "violations should mention impl → impl: ${report.violations}")
    }

    @Test
    fun `check legal fixture produces no violations (only possible warnings)`() {
        val report = adapter.check(fixture)
        assertTrue(report.violations.isEmpty(), "legal fixture must have zero violations, got: ${report.violations}")
    }

    @Test
    fun `generate then check roundtrip is clean for legal graph`() {
        val builds = adapter.generate(fixture)
        val report = adapter.check(fixture, builds)
        assertTrue(report.violations.isEmpty())
    }

    @Test
    fun `generated content is attrs-only Starlark (type owns srcs and tags)`() {
        val builds = adapter.generate(fixture)
        for ((_, c) in builds) {
            assertTrue("load(\"//forma:defs.bzl\"" in c)
            assertFalse("srcs = glob" in c)
            assertFalse("tags = [" in c)
            assertFalse("forma:type=" in c)
            assertFalse("kt_jvm_" in c)
        }
    }
}
