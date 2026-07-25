package tools.forma.deps.core

import dep
import deps
import depsIf
import depsUnless
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import ksp
import tools.forma.config.FormaFeatureFlags
import whenFlag

/**
 * F-099 / GH #126: pure conditional dependency resolution (no Gradle Project).
 */
class ConditionalDependencyTest {

    @Test
    fun `unconditional NameSpec always included`() {
        val spec = NameSpec("g:a:1", Implementation)
        assertTrue(spec.isIncludedBy(FormaFeatureFlags.EMPTY))
        assertTrue(spec.isIncludedBy(FormaFeatureFlags("x" to true)))
    }

    @Test
    fun `depsIf includes only when flag matches expected`() {
        val gated = depsIf("daggerReflect", "com.jakewharton.dagger:dagger-reflect:1".dep)
        val spec = gated.names.single()
        assertEquals("daggerReflect", spec.featureFlag)
        assertTrue(spec.featureFlagExpected)

        assertTrue(spec.isIncludedBy(FormaFeatureFlags("daggerReflect" to true)))
        assertFalse(spec.isIncludedBy(FormaFeatureFlags("daggerReflect" to false)))
        assertFalse(spec.isIncludedBy(FormaFeatureFlags.EMPTY))
    }

    @Test
    fun `depsUnless includes only when flag is off or unknown`() {
        val gated = depsUnless("daggerReflect", "com.google.dagger:dagger:2".dep)
        val spec = gated.names.single()
        assertEquals("daggerReflect", spec.featureFlag)
        assertFalse(spec.featureFlagExpected)

        assertFalse(spec.isIncludedBy(FormaFeatureFlags("daggerReflect" to true)))
        assertTrue(spec.isIncludedBy(FormaFeatureFlags("daggerReflect" to false)))
        assertTrue(spec.isIncludedBy(FormaFeatureFlags.EMPTY))
    }

    @Test
    fun `whenFlag preserves config transitive and GAV`() {
        val base = "g:processor:1".ksp
        val gated = base.whenFlag("useKsp", enabled = true)
        val spec = gated.names.single()
        assertEquals("g:processor:1", spec.name)
        assertEquals(Ksp, spec.config)
        assertTrue(spec.transitive)
        assertEquals("useKsp", spec.featureFlag)
        assertTrue(spec.featureFlagExpected)
    }

    @Test
    fun `resolveFeatureFlags dagger reflect swap recipe`() {
        val always = "com.google.dagger:dagger:2.48".dep
        val reflect = depsIf("daggerReflect", "com.jakewharton.dagger:dagger-reflect:0.3.0".dep)
        val compiler =
            depsUnless("daggerReflect", "com.google.dagger:dagger-compiler:2.48".ksp)
        val composed = deps(always, reflect, compiler)

        val reflectOn =
            composed.resolveFeatureFlags(FormaFeatureFlags("daggerReflect" to true))
                as NamedDependency
        assertEquals(
            listOf(
                "com.google.dagger:dagger:2.48",
                "com.jakewharton.dagger:dagger-reflect:0.3.0",
            ),
            reflectOn.names.map { it.name },
        )

        val reflectOff =
            composed.resolveFeatureFlags(FormaFeatureFlags("daggerReflect" to false))
                as NamedDependency
        assertEquals(
            listOf(
                "com.google.dagger:dagger:2.48",
                "com.google.dagger:dagger-compiler:2.48",
            ),
            reflectOff.names.map { it.name },
        )

        val unknown =
            composed.resolveFeatureFlags(FormaFeatureFlags.EMPTY) as NamedDependency
        // unknown = false → unless keeps compiler, if drops reflect
        assertEquals(
            listOf(
                "com.google.dagger:dagger:2.48",
                "com.google.dagger:dagger-compiler:2.48",
            ),
            unknown.names.map { it.name },
        )
    }

    @Test
    fun `resolve all gated off becomes EmptyDependency`() {
        val onlyIf = depsIf("x", "g:a:1".dep)
        val resolved = onlyIf.resolveFeatureFlags(FormaFeatureFlags("x" to false))
        assertSame(EmptyDependency, resolved)
    }

    @Test
    fun `MixedDependency resolves names only`() {
        val mixed =
            MixedDependency(
                names =
                    listOf(
                        NameSpec("g:a:1", Implementation),
                        NameSpec(
                            "g:b:1",
                            Implementation,
                            featureFlag = "flag",
                            featureFlagExpected = true,
                        ),
                    ),
                targets = emptyList(),
                files = emptyList(),
            )
        val resolved = mixed.resolveFeatureFlags(FormaFeatureFlags.EMPTY) as MixedDependency
        assertEquals(listOf("g:a:1"), resolved.names.map { it.name })
    }

    @Test
    fun `depsIf vararg flattens multiple named deps under one flag`() {
        val gated =
            depsIf(
                "bundle",
                "g:a:1".dep,
                "g:b:1".dep,
            )
        assertEquals(2, gated.names.size)
        assertTrue(gated.names.all { it.featureFlag == "bundle" && it.featureFlagExpected })
        val on = gated.resolveFeatureFlags(FormaFeatureFlags("bundle" to true)) as NamedDependency
        assertEquals(listOf("g:a:1", "g:b:1"), on.names.map { it.name })
    }

    @Test
    fun `EmptyDependency resolve is identity`() {
        assertSame(EmptyDependency, EmptyDependency.resolveFeatureFlags(FormaFeatureFlags.EMPTY))
    }
}
