package tools.forma.core.validation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ContentRuleTest {

    @Test
    fun `NoResourcesUnderMain accepts clean tree`() {
        assertNull(NoResourcesUnderMain.check(listOf("java", "kotlin", "AndroidManifest.xml")))
    }

    @Test
    fun `NoResourcesUnderMain rejects when res present`() {
        val msg = NoResourcesUnderMain.check(listOf("java", "res", "kotlin"))
        assertEquals("Please make sure this does not contain `res` directory", msg)
    }

    @Test
    fun `OnlyResourcesUnderMain accepts exactly res`() {
        assertNull(OnlyResourcesUnderMain.check(listOf("res")))
    }

    @Test
    fun `OnlyResourcesUnderMain rejects other or multiple`() {
        assertEquals(
            "Please make sure this target only contains `res` folder in `src/main`",
            OnlyResourcesUnderMain.check(listOf("java"))
        )
        assertEquals(
            "Please make sure this target only contains `res` folder in `src/main`",
            OnlyResourcesUnderMain.check(listOf("res", "java"))
        )
        assertEquals(
            "Please make sure this target only contains `res` folder in `src/main`",
            OnlyResourcesUnderMain.check(emptyList())
        )
    }

    @Test
    fun `OnlyLayoutResources accepts only layout dirs`() {
        assertNull(OnlyLayoutResources.check(listOf("layout", "layout-land", "layout-sw600dp")))
    }

    @Test
    fun `OnlyLayoutResources rejects non-layout under res`() {
        val msg = OnlyLayoutResources.check(listOf("layout", "drawable", "values"))
        assertEquals("Please make sure this target only contains `layout.*` folders in `src/main/res`", msg)
    }

    @Test
    fun `OnlyLayoutResources accepts empty (historical all-on-empty)`() {
        assertNull(OnlyLayoutResources.check(emptyList()))
    }
}
