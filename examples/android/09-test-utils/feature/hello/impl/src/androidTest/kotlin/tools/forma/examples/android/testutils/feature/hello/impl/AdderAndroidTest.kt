package tools.forma.examples.android.testutils.feature.hello.impl

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import tools.forma.examples.android.testutils.common.androidtestutil.AndroidChecks

/**
 * Instrumented test that **uses** first-party [androidTestUtil] helpers
 * (`AndroidChecks`) via `androidTestDependencies`.
 */
@RunWith(AndroidJUnit4::class)
class AdderAndroidTest {
    @Test
    fun addsOnDeviceClasspath() {
        val sum = Adder.add(4, 5)
        AndroidChecks.touchPositive(sum)
        assertEquals(9, sum)
    }
}
