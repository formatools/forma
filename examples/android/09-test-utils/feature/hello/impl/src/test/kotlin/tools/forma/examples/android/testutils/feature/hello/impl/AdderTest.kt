package tools.forma.examples.android.testutils.feature.hello.impl

import org.junit.Assert.assertEquals
import org.junit.Test
import tools.forma.examples.android.testutils.common.testutil.assertPositive

class AdderTest {
    @Test
    fun adds() {
        val sum = Adder.add(2, 3)
        assertPositive(sum)
        assertEquals(5, sum)
    }
}
