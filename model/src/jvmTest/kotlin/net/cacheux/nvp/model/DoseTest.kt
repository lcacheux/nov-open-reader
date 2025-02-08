package net.cacheux.nvp.model

import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class DoseTest {
    @Test
    fun testCompareValue() {
        val dose1 = Dose(123456L, 10, true, "ABCD")
        val dose2 = Dose(123456L, 10, false, "ABCD")
        val dose3 = Dose(123456L, 20, true, "ABCD")
        val dose4 = Dose(123456L, 20, true, "ABCD")
        val dose5 = Dose(123646L, 10, false, "ABCD")
        val dose6 = Dose(123456L, 10, true)
        val dose7 = Dose(223646L, 10, false)

        assertTrue(dose1.compareValues(dose2))
        assertTrue(dose3.compareValues(dose4))
        assertFalse(dose1.compareValues(dose6))
        assertFalse(dose5.compareValues(dose7))
        assertFalse(dose4.compareValues(dose1))
    }
}