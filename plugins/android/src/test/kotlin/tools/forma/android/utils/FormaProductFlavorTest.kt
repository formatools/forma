package tools.forma.android.utils

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Pure unit coverage for product-flavor model (F-115 / NiA F7 + F27 library reuse).
 * No Gradle Project / AGP containers — plan resolution is shared by binary and library.
 */
class FormaProductFlavorTest {

    @Test
    fun `resolveProductFlavorPlan empty is EMPTY`() {
        assertEquals(ProductFlavorPlan.EMPTY, resolveProductFlavorPlan(emptyList()))
        assertTrue(ProductFlavorPlan.EMPTY.dimensions.isEmpty())
        assertTrue(ProductFlavorPlan.EMPTY.flavors.isEmpty())
    }

    @Test
    fun `resolveProductFlavorPlan keeps call-site order and unique dimensions`() {
        val plan = resolveProductFlavorPlan(
            listOf(
                FormaProductFlavor(name = "demo", dimension = "contentType", applicationIdSuffix = ".demo"),
                FormaProductFlavor(name = "prod", dimension = "contentType"),
            ),
        )
        assertEquals(listOf("contentType"), plan.dimensions)
        assertEquals(listOf("demo", "prod"), plan.flavors.map { it.name })
        assertEquals(".demo", plan.flavors[0].applicationIdSuffix)
        assertEquals(null, plan.flavors[1].applicationIdSuffix)
    }

    @Test
    fun `resolveProductFlavorPlan preserves multiple dimensions in first-seen order`() {
        val plan = resolveProductFlavorPlan(
            listOf(
                FormaProductFlavor(name = "demo", dimension = "contentType"),
                FormaProductFlavor(name = "free", dimension = "store"),
                FormaProductFlavor(name = "prod", dimension = "contentType"),
                FormaProductFlavor(name = "paid", dimension = "store"),
            ),
        )
        assertEquals(listOf("contentType", "store"), plan.dimensions)
        assertEquals(listOf("demo", "free", "prod", "paid"), plan.flavors.map { it.name })
    }

    @Test
    fun `resolveProductFlavorPlan rejects duplicate flavor names`() {
        val error = assertFailsWith<IllegalArgumentException> {
            resolveProductFlavorPlan(
                listOf(
                    FormaProductFlavor(name = "demo", dimension = "contentType"),
                    FormaProductFlavor(name = "demo", dimension = "other"),
                ),
            )
        }
        assertTrue(error.message!!.contains("Duplicate product flavor name 'demo'"))
    }

    @Test
    fun `FormaProductFlavor rejects blank name or dimension`() {
        assertFailsWith<IllegalArgumentException> {
            FormaProductFlavor(name = " ", dimension = "contentType")
        }
        assertFailsWith<IllegalArgumentException> {
            FormaProductFlavor(name = "demo", dimension = "")
        }
    }

    @Test
    fun `BuildConfigField rejects blank type or value`() {
        assertFailsWith<IllegalArgumentException> {
            BuildConfigField(type = "", value = "\"x\"")
        }
        assertFailsWith<IllegalArgumentException> {
            BuildConfigField(type = "String", value = " ")
        }
    }

    @Test
    fun `NiA-shaped demo prod plan matches upstream contentType dimension`() {
        val plan = resolveProductFlavorPlan(
            listOf(
                FormaProductFlavor(
                    name = "demo",
                    dimension = "contentType",
                    applicationIdSuffix = ".demo",
                    buildConfigFields = mapOf(
                        "NIA_CONTENT_TYPE" to BuildConfigField("String", "\"demo\""),
                    ),
                ),
                FormaProductFlavor(
                    name = "prod",
                    dimension = "contentType",
                    buildConfigFields = mapOf(
                        "NIA_CONTENT_TYPE" to BuildConfigField("String", "\"prod\""),
                    ),
                ),
            ),
        )
        assertEquals(listOf("contentType"), plan.dimensions)
        assertEquals("demo", plan.flavors[0].name)
        assertEquals("prod", plan.flavors[1].name)
        assertEquals(".demo", plan.flavors[0].applicationIdSuffix)
        assertEquals(
            BuildConfigField("String", "\"demo\""),
            plan.flavors[0].buildConfigFields["NIA_CONTENT_TYPE"],
        )
    }

    @Test
    fun `library plan reuses same resolve as binary — APK-only fields stay on model`() {
        // Libraries pass the same FormaProductFlavor list; applyFormaProductFlavor
        // skips applicationIdSuffix on non-ApplicationProductFlavor. Plan is identical.
        val libraryCallSite = listOf(
            FormaProductFlavor(name = "demo", dimension = "contentType", applicationIdSuffix = ".demo"),
            FormaProductFlavor(name = "prod", dimension = "contentType"),
        )
        val plan = resolveProductFlavorPlan(libraryCallSite)
        assertEquals(ProductFlavorPlan.EMPTY, resolveProductFlavorPlan(emptyList()))
        assertEquals(listOf("contentType"), plan.dimensions)
        assertEquals(listOf("demo", "prod"), plan.flavors.map { it.name })
        // Model still carries APK suffix (binary may share the list); library apply ignores it.
        assertEquals(".demo", plan.flavors[0].applicationIdSuffix)
    }
}
