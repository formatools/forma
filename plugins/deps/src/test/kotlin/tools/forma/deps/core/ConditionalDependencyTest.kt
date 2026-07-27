package tools.forma.deps.core

import dep
import deps
import depsIf
import depsUnless
import featureImplementation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import ksp
import org.gradle.testfixtures.ProjectBuilder
import tools.forma.config.FormaFeatureFlags
import tools.forma.target.FormaTarget
import whenFlag

/**
 * F-099 / F-104: pure conditional dependency resolution (no Gradle apply path).
 */
class ConditionalDependencyTest {

    private fun formaTarget(name: String): FormaTarget {
        val project = ProjectBuilder.builder().withName(name).build()
        return FormaTarget(project)
    }

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
    fun `MixedDependency resolves names only historically`() {
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

    // --- F-104: target flag gating + featureImplementation ---

    @Test
    fun `unconditional TargetSpec always included`() {
        val spec = TargetSpec(formaTarget("always-impl"), Implementation)
        assertTrue(spec.isIncludedBy(FormaFeatureFlags.EMPTY))
        assertTrue(spec.isIncludedBy(FormaFeatureFlags(USE_FEATURE_STUBS_FLAG to true)))
    }

    @Test
    fun `TargetSpec whenFlag include and exclude`() {
        val target = formaTarget("hello-impl")
        val gated = deps(target).whenFlag(USE_FEATURE_STUBS_FLAG, enabled = true)
        val spec = gated.targets.single()
        assertEquals(USE_FEATURE_STUBS_FLAG, spec.featureFlag)
        assertTrue(spec.featureFlagExpected)
        assertTrue(spec.isIncludedBy(FormaFeatureFlags(USE_FEATURE_STUBS_FLAG to true)))
        assertFalse(spec.isIncludedBy(FormaFeatureFlags(USE_FEATURE_STUBS_FLAG to false)))
        assertFalse(spec.isIncludedBy(FormaFeatureFlags.EMPTY))
    }

    @Test
    fun `TargetDependency whenFlag preserves ConfigurationType`() {
        val target = formaTarget("lib")
        val base =
            TargetDependency(
                listOf(TargetSpec(target, CompileOnly)),
            )
        val gated = base.whenFlag("x", enabled = false)
        val spec = gated.targets.single()
        assertEquals(CompileOnly, spec.config)
        assertEquals("x", spec.featureFlag)
        assertFalse(spec.featureFlagExpected)

        val runtime =
            TargetDependency(listOf(TargetSpec(target, RuntimeOnly))).whenFlag("y")
        assertEquals(RuntimeOnly, runtime.targets.single().config)
        assertEquals(Implementation, TargetSpec(target).config)
    }

    @Test
    fun `depsIf and depsUnless on FormaTarget`() {
        val impl = formaTarget("f-impl")
        val stub = formaTarget("f-stub-impl")
        val ifOn = depsIf(USE_FEATURE_STUBS_FLAG, stub)
        val unless = depsUnless(USE_FEATURE_STUBS_FLAG, impl)

        assertEquals(1, ifOn.targets.size)
        assertTrue(ifOn.targets.single().featureFlagExpected)
        assertFalse(unless.targets.single().featureFlagExpected)

        val composed = deps(ifOn, unless)
        val stubsOn =
            composed.resolveFeatureFlags(FormaFeatureFlags(USE_FEATURE_STUBS_FLAG to true))
                as TargetDependency
        assertEquals(listOf("f-stub-impl"), stubsOn.targets.map { it.target.name })

        val stubsOff =
            composed.resolveFeatureFlags(FormaFeatureFlags(USE_FEATURE_STUBS_FLAG to false))
                as TargetDependency
        assertEquals(listOf("f-impl"), stubsOff.targets.map { it.target.name })

        val unknown = composed.resolveFeatureFlags(FormaFeatureFlags.EMPTY) as TargetDependency
        assertEquals(listOf("f-impl"), unknown.targets.map { it.target.name })
    }

    @Test
    fun `resolveFeatureFlags filters TargetDependency to EmptyDependency`() {
        val onlyStub = depsIf(USE_FEATURE_STUBS_FLAG, formaTarget("stub-impl"))
        val resolved =
            onlyStub.resolveFeatureFlags(FormaFeatureFlags(USE_FEATURE_STUBS_FLAG to false))
        assertSame(EmptyDependency, resolved)
    }

    @Test
    fun `MixedDependency filters targets and keeps platforms`() {
        val impl = formaTarget("hello-impl")
        val stub = formaTarget("hello-stub-impl")
        val mixed =
            MixedDependency(
                names =
                    listOf(
                        NameSpec(
                            "g:optional:1",
                            Implementation,
                            featureFlag = "extra",
                            featureFlagExpected = true,
                        ),
                    ),
                targets =
                    featureImplementationPair(
                        impl = listOf(TargetSpec(impl)),
                        stub = listOf(TargetSpec(stub)),
                    ),
                files = emptyList(),
                platforms = listOf(PlatformSpec("g:bom:1", Implementation)),
            )

        val off = mixed.resolveFeatureFlags(FormaFeatureFlags.EMPTY) as MixedDependency
        assertTrue(off.names.isEmpty())
        assertEquals(listOf("hello-impl"), off.targets.map { it.target.name })
        assertEquals(listOf("g:bom:1"), off.platforms.map { it.name })

        val on =
            mixed.resolveFeatureFlags(
                FormaFeatureFlags(USE_FEATURE_STUBS_FLAG to true, "extra" to true),
            ) as MixedDependency
        assertEquals(listOf("g:optional:1"), on.names.map { it.name })
        assertEquals(listOf("hello-stub-impl"), on.targets.map { it.target.name })
        assertEquals(1, on.platforms.size)
    }

    @Test
    fun `featureImplementation resolves impl-only vs stub-only`() {
        val impl = formaTarget("feature-hello-impl")
        val stub = formaTarget("feature-hello-stub-impl")
        val pair = featureImplementation(impl = impl, stub = stub)

        assertEquals(2, pair.targets.size)
        assertEquals(USE_FEATURE_STUBS_FLAG, pair.targets[0].featureFlag)
        assertFalse(pair.targets[0].featureFlagExpected)
        assertTrue(pair.targets[1].featureFlagExpected)
        assertEquals(Implementation, pair.targets[0].config)
        assertEquals(Implementation, pair.targets[1].config)

        val production =
            pair.resolveFeatureFlags(FormaFeatureFlags.EMPTY) as TargetDependency
        assertEquals(listOf("feature-hello-impl"), production.targets.map { it.target.name })

        val stubMode =
            pair.resolveFeatureFlags(FormaFeatureFlags(USE_FEATURE_STUBS_FLAG to true))
                as TargetDependency
        assertEquals(listOf("feature-hello-stub-impl"), stubMode.targets.map { it.target.name })

        val explicitOff =
            pair.resolveFeatureFlags(FormaFeatureFlags(USE_FEATURE_STUBS_FLAG to false))
                as TargetDependency
        assertEquals(listOf("feature-hello-impl"), explicitOff.targets.map { it.target.name })
    }

    @Test
    fun `featureImplementation preserves custom ConfigurationType on both sides`() {
        val impl = formaTarget("i")
        val stub = formaTarget("s")
        val pair =
            featureImplementation(
                impl = TargetDependency(listOf(TargetSpec(impl, CompileOnly))),
                stub = TargetDependency(listOf(TargetSpec(stub, RuntimeOnly))),
            )
        assertEquals(CompileOnly, pair.targets[0].config)
        assertEquals(RuntimeOnly, pair.targets[1].config)
    }

    @Test
    fun `featureImplementationPair rejects empty sides`() {
        val t = TargetSpec(formaTarget("x"))
        try {
            featureImplementationPair(impl = emptyList(), stub = listOf(t))
            kotlin.test.fail("expected require failure for empty impl")
        } catch (_: IllegalArgumentException) {
            // expected
        }
        try {
            featureImplementationPair(impl = listOf(t), stub = emptyList())
            kotlin.test.fail("expected require failure for empty stub")
        } catch (_: IllegalArgumentException) {
            // expected
        }
    }

    @Test
    fun `canonical flag constant matches documented name`() {
        assertEquals("useFeatureStubs", USE_FEATURE_STUBS_FLAG)
        assertEquals("forma.useFeatureStubs", USE_FEATURE_STUBS_PROPERTY)
    }
}
