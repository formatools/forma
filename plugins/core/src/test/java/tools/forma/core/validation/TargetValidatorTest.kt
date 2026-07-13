package tools.forma.core.validation

import tools.forma.core.target.SuffixNameMatcher
import tools.forma.core.target.TargetRef
import tools.forma.core.target.targetType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TargetValidatorTest {

    private data class TestRef(override val name: String) : TargetRef

    private val api = targetType("android.api", "api")
    private val impl = targetType("android.impl", "impl")
    private val library = targetType("android.library", "library")
    private val res = targetType("android.res", "res")

    @Test
    fun `AcceptAny never throws`() {
        AcceptAny.validate(TestRef("anything"))
        AcceptAny.validate(TestRef("impl"))
        // no exception
    }

    @Test
    fun `dependencyTypeValidator accepts exact suffix match`() {
        val v = dependencyTypeValidator(listOf(api))
        v.validate(TestRef("api"))
    }

    @Test
    fun `dependencyTypeValidator accepts dashed suffix match`() {
        val v = dependencyTypeValidator(listOf(impl))
        v.validate(TestRef("feature-characters-impl"))
        v.validate(TestRef("my-impl"))
    }

    @Test
    fun `dependencyTypeValidator rejects wrong suffix with recognizable message`() {
        val v = dependencyTypeValidator(listOf(api))
        val ex = assertFailsWith<FormaValidationException> {
            v.validate(TestRef("impl"))
        }
        assertTrue("name does not match allowed target type(s)" in ex.message.orEmpty())
        assertTrue("Allowed name suffix(es): api" in ex.message.orEmpty())
    }

    @Test
    fun `dependencyTypeValidator multi-type allow list`() {
        val v = dependencyTypeValidator(listOf(api, library))
        v.validate(TestRef("api"))
        v.validate(TestRef("library"))
        v.validate(TestRef("core-library"))
        val ex = assertFailsWith<FormaValidationException> {
            v.validate(TestRef("impl"))
        }
        assertTrue("Allowed name suffix(es): api, library" in ex.message.orEmpty())
    }

    @Test
    fun `selfTypeValidator enforces single expected type`() {
        val v = selfTypeValidator(impl)
        v.validate(TestRef("impl"))
        v.validate(TestRef("my-feature-impl"))
        assertFailsWith<FormaValidationException> {
            v.validate(TestRef("api"))
        }
    }

    @Test
    fun `identity cache returns same validator instance for same TargetType ref (===)`() {
        val t = targetType("test.foo", "foo")
        val v1 = dependencyTypeValidator(listOf(t))
        val v2 = dependencyTypeValidator(listOf(t))
        assertSame(v1, v2, "same TargetType instance must yield === validator")

        val vs1 = selfTypeValidator(t)
        val vs2 = selfTypeValidator(t)
        assertSame(vs1, vs2)
    }

    @Test
    fun `identity cache for multi also returns cached instance`() {
        val t1 = targetType("x.a", "a")
        val t2 = targetType("x.b", "b")
        val list = listOf(t1, t2)
        val v1 = dependencyTypeValidator(list)
        val v2 = dependencyTypeValidator(list) // same list ref
        assertSame(v1, v2)

        // different list obj but equal content should also hit (CHM + List.equals)
        val v3 = dependencyTypeValidator(listOf(t1, t2))
        assertSame(v1, v3)
    }

    @Test
    fun `custom NameMatcher is honored`() {
        val strict = object : tools.forma.core.target.NameMatcher {
            override fun matches(projectName: String, type: tools.forma.core.target.TargetType): Boolean =
                projectName == type.nameSuffix
        }
        val v = dependencyTypeValidator(listOf(api), strict)
        v.validate(TestRef("api"))
        assertFailsWith<FormaValidationException> {
            v.validate(TestRef("foo-api"))
        }
    }
}
